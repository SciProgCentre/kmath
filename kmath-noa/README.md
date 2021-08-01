# Module kmath-noa

A general purpose differentiable computation library over
[NOA](https://github.com/grinisrit/noa.git)
together with relevant functionality from 
[LibTorch](https://pytorch.org/cppdocs). 

Our aim is to cover a wide set of applications 
from bayesian optimisation and deep learning to particle physics
simulations. In fact, we support any 
differentiable program written on top of 
`AutoGrad` & `ATen`.

## Installation from source

Currently, we support only
the [GNU](https://gcc.gnu.org/) toolchain for the native artifacts.
For `GPU` kernels, we require a compatible
[CUDA](https://docs.nvidia.com/cuda/cuda-installation-guide-linux/index.html)
installation. If you are on Windows, we recommend setting up
everything on [WSL](https://docs.nvidia.com/cuda/wsl-user-guide/index.html).

To install the library, you can simply publish to the local
Maven repository:
```
./gradlew -q :kmath-noa:publishToMavenLocal
```
This will fetch and build the `JNI` wrapper `jnoa`. 

In your own application add the local dependency:
```kotlin
repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    implementation("space.kscience:kmath-noa:0.3.0-dev-14")
}
```
To load the native library you will need to add to the VM options:
```
-Djava.library.path=${HOME}/.konan/third-party/noa-v0.0.1/cpp-build/jnoa
```

## Usage

The library is under active development. Many more features
will be available soon.

### Tensors and Linear Algebra

We implement the tensor algebra interfaces 
from [kmath-tensors](../kmath-tensors):
```kotlin
NoaFloat {
    val tensor = 
        randNormal(
            shape = intArrayOf(7, 5, 3), 
            device = Device.CPU) // or Device.CUDA(0) for GPU
    
    // Compute SVD
    val (tensorU, tensorS, tensorV) = tensor.svd()
    
    // Reconstruct tensor
    val tensorReg =
        tensorU dot (diagonalEmbedding(tensorS) dot tensorV.transpose(-2, -1))
    
    // Serialise tensor for later
    tensorReg.save("tensorReg.pt")
}
```

The saved tensor can be loaded in `C++` or in `python`:
```python
import torch
tensor_reg = list(torch.jit.load('tensorReg.pt').parameters())[0]
```

The most efficient way passing data between the `JVM` and the native backend
is to rely on primitive arrays: 
```kotlin
val array = (1..8).map { 100f * it }.toFloatArray()
val updateArray = floatArrayOf(15f, 20f)
val resArray = NoaFloat {
    val tensor = copyFromArray(array, intArrayOf(2, 2, 2))
    NoaFloat {
        // The call `tensor[0]` creates a native tensor instance pointing to a slice of `tensor`
        // The second call `[1]` is a setter call and does not create any new instances
        tensor[0][1] = updateArray
        // The instance `tensor[0]` is destroyed as we move out of the scope
    }!! // if the computation fails the result fill be null
    tensor.copyToArray()
    // the instance `tensor` is destroyed here
}!!

```

### Automatic Differentiation
The [AutoGrad](https://pytorch.org/tutorials/beginner/blitz/autograd_tutorial.html)
engine is exposed:
```kotlin
NoaFloat {
    // Create a quadratic function
    val dim = 3
    val tensorX = randNormal(shape = intArrayOf(dim))
    val randFeatures = randNormal(shape = intArrayOf(dim, dim))
    val tensorSigma = randFeatures + randFeatures.transpose(0, 1)
    val tensorMu = randNormal(shape = intArrayOf(dim))

    // Create a differentiable expression
    val expressionAtX = withGradAt(tensorX) { x ->
        0.5f * (x dot (tensorSigma dot x)) + (tensorMu dot x) + 25.9f
    }

    // Evaluate the gradient at tensorX
    // retaining the graph for the hessian computation
    val gradientAtX = expressionAtX.autoGradient(tensorX, retainGraph = true)
    
    // Compute the hessian at tensorX
    val hessianAtX = expressionAtX.autoHessian(tensorX)
}
```
### Deep Learning
You can train any [TorchScript](https://pytorch.org/docs/stable/jit.html) model.
For example, you can build in `python` the following neural network
and prepare the training data:

```python
import torch

n_tr = 7
n_val = 300
x_val = torch.linspace(-5, 5, n_val).view(-1, 1)
y_val = torch.sin(x_val)
x_train = torch.linspace(-3.14, 3.14, n_tr).view(-1, 1)
y_train = torch.sin(x_train) + torch.randn_like(x_train) * 0.1

class Data(torch.nn.Module):
    def __init__(self):
        super(Data, self).__init__()
        self.register_buffer('x_val', x_val)
        self.register_buffer('y_val', y_val)
        self.register_buffer('x_train', x_train)
        self.register_buffer('y_train', y_train)

class Net(torch.nn.Module):
    def __init__(self):
        super(Net, self).__init__()
        self.l1 = torch.nn.Linear(1, 10, bias = True)
        self.l2 = torch.nn.Linear(10, 10, bias = True)
        self.l3 = torch.nn.Linear(10, 1, bias = True)

    def forward(self, x):
        x = self.l1(x)
        x = torch.relu(x)
        x = self.l2(x)
        x = torch.relu(x)
        x = self.l3(x)
        return x

class Loss(torch.nn.Module):
    def __init__(self, target):
        super(Loss, self).__init__()
        self.register_buffer('target', target)
        self.loss = torch.nn.MSELoss()
        
    def forward(self, x):
        return self.loss(x, self.target)

# Generate TorchScript modules and serialise them
torch.jit.script(Data()).save('data.pt')
torch.jit.script(Net()).save('net.pt')
torch.jit.script(Loss(y_train)).save('loss.pt')
```

You can then load the modules into `kotlin` and train them:
```kotlin
NoaFloat { 
    
    // Load the serialised JIT modules
    // The training data
    val dataModule = loadJitModule("data.pt")
    // The DL model
    val netModule = loadJitModule("net.pt")
    // The loss function
    val lossModule = loadJitModule("loss.pt")

    // Get the tensors from the module
    val xTrain = dataModule.getBuffer("x_train")
    val yTrain = dataModule.getBuffer("y_train")
    val xVal = dataModule.getBuffer("x_val")
    val yVal = dataModule.getBuffer("y_val")

    // Set the model in training mode
    netModule.train(true)
    // Loss function for training 
    lossModule.setBuffer("target", yTrain)

    // Compute the predictions
    val yPred = netModule.forward(xTrain)
    // Compute the training loss
    val loss = lossModule.forward(yPred)
    println(loss)
    
    // Set-up the Adam optimiser with learning rate 0.005
    val optimiser = netModule.adamOptimiser(0.005)

    // Train for 250 epochs
    repeat(250){
        // Clean gradients
        optimiser.zeroGrad()
        // Use forwardAssign to for better memory management
        netModule.forwardAssign(xTrain, yPred)
        lossModule.forwardAssign(yPred, loss)
        // Backward pass
        loss.backward()
        // Update model parameters 
        optimiser.step()
        if(it % 50 == 0)
            println("Training loss: $loss")
    }

    // Finally validate the model 
    // Compute the predictions for the validation features
    netModule.forwardAssign(xVal, yPred)
    // Set the loss for validation 
    lossModule.setBuffer("target", yVal)
    // Compute the loss on validation dataset
    lossModule.forwardAssign(yPred, loss)
    println("Validation loss: $loss")
    
    // The model can be serialised in its current state
    netModule.save("trained_net.pt")
}

```

### Custom memory management
Native memory management relies on scoping 
with [NoaScope](src/main/kotlin/space/kscience/kmath/noa/memory/NoaScope.kt)
which is readily available within an algebra context.
Manual management is also possible:
```kotlin
// Create a scope
val scope = NoaScope()

val tensor = NoaFloat(scope){
    full(5f, intArrayOf(1))
}!! // the result might be null

// If the computation fails resources will be freed automatically
// Otherwise it's your responsibility:
scope.disposeAll()

// Attempts to use tensor here is undefined behaviour
```

For more examples have a look at 
[NOA](https://github.com/grinisrit/noa) docs.

Contributed by [Roland Grinis](https://github.com/grinisrit)

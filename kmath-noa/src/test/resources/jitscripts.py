import torch

torch.manual_seed(987654)

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


torch.jit.script(Data()).save('data.pt')

torch.jit.script(Net()).save('net.pt')

torch.jit.script(Loss(y_train)).save('loss.pt')

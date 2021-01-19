#include <torch/torch.h>

namespace ctorch
{

    using TorchTensorHandle = void *;

    template <typename Dtype>
    inline c10::ScalarType dtype()
    {
        return torch::kFloat64;
    }

    template <>
    inline c10::ScalarType dtype<float>()
    {
        return torch::kFloat32;
    }

    template <>
    inline c10::ScalarType dtype<long>()
    {
        return torch::kInt64;
    }

    template <>
    inline c10::ScalarType dtype<int>()
    {
        return torch::kInt32;
    }

    template <typename Handle>
    inline torch::Tensor &cast(const Handle &tensor_handle)
    {
        return *static_cast<torch::Tensor *>((TorchTensorHandle)tensor_handle);
    }

    template <typename Handle>
    inline void dispose_tensor(const Handle &tensor_handle)
    {
        delete static_cast<torch::Tensor *>((TorchTensorHandle)tensor_handle);
    }

    inline std::string tensor_to_string(const torch::Tensor &tensor)
    {
        std::stringstream bufrep;
        bufrep << tensor;
        return bufrep.str();
    }

    inline char *tensor_to_char(const torch::Tensor &tensor)
    {
        auto rep = tensor_to_string(tensor);
        char *crep = (char *)malloc(rep.length() + 1);
        std::strcpy(crep, rep.c_str());
        return crep;
    }

    inline int device_to_int(const torch::Tensor &tensor)
    {
        return (tensor.device().type() == torch::kCPU) ? 0 : 1 + tensor.device().index();
    }

    inline torch::Device int_to_device(int device_int)
    {
        return (device_int == 0) ? torch::kCPU : torch::Device(torch::kCUDA, device_int - 1);
    }

    inline std::vector<int64_t> to_vec_int(int *arr, int arr_size)
    {
        auto vec = std::vector<int64_t>(arr_size);
        vec.assign(arr, arr + arr_size);
        return vec;
    }

    inline std::vector<at::indexing::TensorIndex> to_index(int *arr, int arr_size)
    {
        std::vector<at::indexing::TensorIndex> index;
        for (int i = 0; i < arr_size; i++)
        {
            index.emplace_back(arr[i]);
        }
        return index;
    }

    template <typename Dtype>
    inline torch::Tensor from_blob(Dtype *data, const std::vector<int64_t> &shape, torch::Device device, bool copy)
    {
        return torch::from_blob(data, shape, dtype<Dtype>()).to(torch::TensorOptions().layout(torch::kStrided).device(device), false, copy);
    }

    template <typename NumType>
    inline NumType get(const torch::Tensor &tensor, int *index)
    {
        return tensor.index(to_index(index, tensor.dim())).item<NumType>();
    }

    template <typename NumType>
    inline void set(const torch::Tensor &tensor, int *index, NumType value)
    {
        tensor.index(to_index(index, tensor.dim())) = value;
    }

    template <typename Dtype>
    inline torch::Tensor randn(const std::vector<int64_t> &shape, torch::Device device)
    {
        return torch::randn(shape, torch::TensorOptions().dtype(dtype<Dtype>()).layout(torch::kStrided).device(device));
    }

    template <typename Dtype>
    inline torch::Tensor rand(const std::vector<int64_t> &shape, torch::Device device)
    {
        return torch::rand(shape, torch::TensorOptions().dtype(dtype<Dtype>()).layout(torch::kStrided).device(device));
    }

    template <typename Dtype>
    inline torch::Tensor randint(long low, long high, const std::vector<int64_t> &shape, torch::Device device)
    {
        return torch::randint(low, high, shape, torch::TensorOptions().dtype(dtype<Dtype>()).layout(torch::kStrided).device(device));
    }

    template <typename Dtype>
    inline torch::Tensor full(Dtype value, const std::vector<int64_t> &shape, torch::Device device)
    {
        return torch::full(shape, value, torch::TensorOptions().dtype(dtype<Dtype>()).layout(torch::kStrided).device(device));
    }

    inline torch::Tensor hessian(const torch::Tensor &value, const torch::Tensor &variable)
    {
        auto nelem = variable.numel();
        auto hess = value.new_zeros({nelem, nelem});
        auto grad = torch::autograd::grad({value}, {variable}, {}, torch::nullopt, true)[0].view(nelem);
        int i = 0;
        for (int j = 0; j < nelem; j++)
        {
            auto row = grad[j].requires_grad()
                           ? torch::autograd::grad({grad[i]}, {variable}, {}, true, true, true)[0].view(nelem).slice(0, j, nelem)
                           : grad[j].new_zeros(nelem - j);
            hess[i].slice(0, i, nelem).add_(row.type_as(hess));
            i++;
        }
        auto ndim = variable.dim();
        auto sizes = variable.sizes().data();
        auto shape = std::vector<int64_t>(ndim);
        shape.assign(sizes, sizes + ndim);
        shape.reserve(2 * ndim);
        std::copy_n(shape.begin(), ndim, std::back_inserter(shape));
        return (hess + torch::triu(hess, 1).t()).view(shape);
    }

} // namespace ctorch

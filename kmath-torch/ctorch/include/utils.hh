#include <torch/torch.h>

#include "ctorch.h"

namespace ctorch
{
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

    inline torch::Tensor &cast(const TorchTensorHandle &tensor_handle)
    {
        return *static_cast<torch::Tensor *>(tensor_handle);
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
    inline torch::Tensor from_blob(Dtype *data, std::vector<int64_t> shape, torch::Device device, bool copy)
    {
        return torch::from_blob(data, shape, dtype<Dtype>()).to(torch::TensorOptions().layout(torch::kStrided).device(device), false, copy);
    }

    template <typename NumType>
    inline NumType get(const TorchTensorHandle &tensor_handle, int *index)
    {
        auto ten = ctorch::cast(tensor_handle);
        return ten.index(to_index(index, ten.dim())).item<NumType>();
    }

    template <typename NumType>
    inline void set(TorchTensorHandle &tensor_handle, int *index, NumType value)
    {
        auto ten = ctorch::cast(tensor_handle);
        ten.index(to_index(index, ten.dim())) = value;
    }

    template <typename Dtype>
    inline torch::Tensor randn(std::vector<int64_t> shape, torch::Device device)
    {
        return torch::randn(shape, torch::TensorOptions().dtype(dtype<Dtype>()).layout(torch::kStrided).device(device));
    }

    template <typename Dtype>
    inline torch::Tensor rand(std::vector<int64_t> shape, torch::Device device)
    {
        return torch::rand(shape, torch::TensorOptions().dtype(dtype<Dtype>()).layout(torch::kStrided).device(device));
    }

    template <typename Dtype>
    inline torch::Tensor randint(Dtype low, Dtype high, std::vector<int64_t> shape, torch::Device device)
    {
        return torch::randint(low, high, shape, torch::TensorOptions().dtype(dtype<Dtype>()).layout(torch::kStrided).device(device));
    }

    template <typename Dtype>
    inline torch::Tensor full(Dtype value, std::vector<int64_t> shape, torch::Device device)
    {
        return torch::full(shape, value, torch::TensorOptions().dtype(dtype<Dtype>()).layout(torch::kStrided).device(device));
    }

} // namespace ctorch

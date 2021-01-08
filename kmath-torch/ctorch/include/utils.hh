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

    inline std::vector<int64_t> to_vec_int(int *arr, int arr_size)
    {
        auto vec = std::vector<int64_t>(arr_size);
        vec.assign(arr, arr + arr_size);
        return vec;
    }

    template <typename Dtype>
    inline torch::Tensor copy_from_blob(Dtype *data, std::vector<int64_t> shape, torch::Device device)
    {
        return torch::from_blob(data, shape, dtype<Dtype>()).to(torch::TensorOptions().layout(torch::kStrided).device(device), false, true);
    }

    inline int *to_dynamic_ints(const c10::IntArrayRef &arr)
    {
        size_t n = arr.size();
        int *res = (int *)malloc(sizeof(int) * n);
        for (size_t i = 0; i < n; i++)
        {
            res[i] = arr[i];
        }
        return res;
    }

    inline std::vector<at::indexing::TensorIndex> offset_to_index(int offset, const c10::IntArrayRef &strides)
    {
        std::vector<at::indexing::TensorIndex> index;
        for (const auto &stride : strides)
        {
            index.emplace_back(offset / stride);
            offset %= stride;
        }
        return index;
    }

    template <typename NumType>
    inline NumType get_at_offset(const TorchTensorHandle &tensor_handle, int offset)
    {
        auto ten = ctorch::cast(tensor_handle);
        return ten.index(ctorch::offset_to_index(offset, ten.strides())).item<NumType>();
    }

    template <typename NumType>
    inline void set_at_offset(TorchTensorHandle &tensor_handle, int offset, NumType value)
    {
        auto ten = ctorch::cast(tensor_handle);
        ten.index(offset_to_index(offset, ten.strides())) = value;
    }

    template <typename Dtype>
    inline torch::Tensor randn(std::vector<int64_t> shape, torch::Device device)
    {
        return torch::randn(shape, torch::TensorOptions().dtype(dtype<Dtype>()).layout(torch::kStrided).device(device));
    }

} // namespace ctorch

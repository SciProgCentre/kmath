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

    inline torch::Tensor &cast(TorchTensorHandle tensor_handle)
    {
        return *static_cast<torch::Tensor *>(tensor_handle);
    }

    template <typename Dtype>
    inline torch::Tensor copy_from_blob(Dtype *data, int *shape, int dim)
    {
        auto shape_vec = std::vector<int64_t>(dim);
        shape_vec.assign(shape, shape + dim);
        return torch::from_blob(data, shape_vec, dtype<Dtype>()).clone();
    }

    template <typename IntArray>
    inline int *to_dynamic_ints(IntArray arr)
    {
        size_t n = arr.size();
        int *res = (int *)malloc(sizeof(int) * n);
        for (size_t i = 0; i < n; i++)
        {
            res[i] = arr[i];
        }
        return res;
    }

} // namespace ctorch

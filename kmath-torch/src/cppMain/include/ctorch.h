#ifndef CTORCH
#define CTORCH

#include <stdbool.h>

#ifdef __cplusplus
extern "C"
{
#endif

    typedef void *TorchTensorHandle;

    int get_num_threads();

    void set_num_threads(int num_threads);

    bool cuda_is_available();

    void set_seed(int seed);

    TorchTensorHandle empty_tensor();

    TorchTensorHandle from_blob_double(double *data, int *shape, int dim, int device, bool copy);
    TorchTensorHandle from_blob_float(float *data, int *shape, int dim, int device, bool copy);
    TorchTensorHandle from_blob_long(long *data, int *shape, int dim, int device, bool copy);
    TorchTensorHandle from_blob_int(int *data, int *shape, int dim, int device, bool copy);
    TorchTensorHandle copy_tensor(TorchTensorHandle tensor_handle);
    TorchTensorHandle copy_to_device(TorchTensorHandle tensor_handle, int device);
    TorchTensorHandle copy_to_double(TorchTensorHandle tensor_handle);
    TorchTensorHandle copy_to_float(TorchTensorHandle tensor_handle);
    TorchTensorHandle copy_to_long(TorchTensorHandle tensor_handle);
    TorchTensorHandle copy_to_int(TorchTensorHandle tensor_handle);
    void swap_tensors(TorchTensorHandle lhs_handle, TorchTensorHandle rhs_handle);
    TorchTensorHandle view_tensor(TorchTensorHandle tensor_handle, int *shape, int dim);

    char *tensor_to_string(TorchTensorHandle tensor_handle);
    void dispose_char(char *ptr);
    void dispose_tensor(TorchTensorHandle tensor_handle);

    int get_dim(TorchTensorHandle tensor_handle);
    int get_numel(TorchTensorHandle tensor_handle);
    int get_shape_at(TorchTensorHandle tensor_handle, int d);
    int get_stride_at(TorchTensorHandle tensor_handle, int d);
    int get_device(TorchTensorHandle tensor_handle);

    double *get_data_double(TorchTensorHandle tensor_handle);
    float *get_data_float(TorchTensorHandle tensor_handle);
    long *get_data_long(TorchTensorHandle tensor_handle);
    int *get_data_int(TorchTensorHandle tensor_handle);

    double get_item_double(TorchTensorHandle tensor_handle);
    float get_item_float(TorchTensorHandle tensor_handle);
    long get_item_long(TorchTensorHandle tensor_handle);
    int get_item_int(TorchTensorHandle tensor_handle);

    double get_double(TorchTensorHandle tensor_handle, int *index);
    float get_float(TorchTensorHandle tensor_handle, int *index);
    long get_long(TorchTensorHandle tensor_handle, int *index);
    int get_int(TorchTensorHandle tensor_handle, int *index);
    void set_double(TorchTensorHandle tensor_handle, int *index, double value);
    void set_float(TorchTensorHandle tensor_handle, int *index, float value);
    void set_long(TorchTensorHandle tensor_handle, int *index, long value);
    void set_int(TorchTensorHandle tensor_handle, int *index, int value);

    TorchTensorHandle rand_double(int *shape, int shape_size, int device);
    TorchTensorHandle randn_double(int *shape, int shape_size, int device);
    TorchTensorHandle rand_float(int *shape, int shape_size, int device);
    TorchTensorHandle randn_float(int *shape, int shape_size, int device);

    TorchTensorHandle randint_double(long low, long high, int *shape, int shape_size, int device);
    TorchTensorHandle randint_float(long low, long high, int *shape, int shape_size, int device);
    TorchTensorHandle randint_long(long low, long high, int *shape, int shape_size, int device);
    TorchTensorHandle randint_int(int low, int high, int *shape, int shape_size, int device);

    TorchTensorHandle rand_like(TorchTensorHandle tensor_handle);
    void rand_like_assign(TorchTensorHandle tensor_handle);
    TorchTensorHandle randn_like(TorchTensorHandle tensor_handle);
    void randn_like_assign(TorchTensorHandle tensor_handle);
    TorchTensorHandle randint_long_like(TorchTensorHandle tensor_handle, long low, long high);
    void randint_long_like_assign(TorchTensorHandle tensor_handle, long low, long high);
    TorchTensorHandle randint_int_like(TorchTensorHandle tensor_handle, int low, int high);
    void randint_int_like_assign(TorchTensorHandle tensor_handle, int low, int high);

    TorchTensorHandle full_double(double value, int *shape, int shape_size, int device);
    TorchTensorHandle full_float(float value, int *shape, int shape_size, int device);
    TorchTensorHandle full_long(long value, int *shape, int shape_size, int device);
    TorchTensorHandle full_int(int value, int *shape, int shape_size, int device);

    TorchTensorHandle times_double(double value, TorchTensorHandle other);
    TorchTensorHandle times_float(float value, TorchTensorHandle other);
    TorchTensorHandle times_long(long value, TorchTensorHandle other);
    TorchTensorHandle times_int(int value, TorchTensorHandle other);

    void times_double_assign(double value, TorchTensorHandle other);
    void times_float_assign(float value, TorchTensorHandle other);
    void times_long_assign(long value, TorchTensorHandle other);
    void times_int_assign(int value, TorchTensorHandle other);

    TorchTensorHandle plus_double(double value, TorchTensorHandle other);
    TorchTensorHandle plus_float(float value, TorchTensorHandle other);
    TorchTensorHandle plus_long(long value, TorchTensorHandle other);
    TorchTensorHandle plus_int(int value, TorchTensorHandle other);

    void plus_double_assign(double value, TorchTensorHandle other);
    void plus_float_assign(float value, TorchTensorHandle other);
    void plus_long_assign(long value, TorchTensorHandle other);
    void plus_int_assign(int value, TorchTensorHandle other);

    TorchTensorHandle plus_tensor(TorchTensorHandle lhs, TorchTensorHandle rhs);
    void plus_tensor_assign(TorchTensorHandle lhs, TorchTensorHandle rhs);
    TorchTensorHandle minus_tensor(TorchTensorHandle lhs, TorchTensorHandle rhs);
    void minus_tensor_assign(TorchTensorHandle lhs, TorchTensorHandle rhs);
    TorchTensorHandle times_tensor(TorchTensorHandle lhs, TorchTensorHandle rhs);
    void times_tensor_assign(TorchTensorHandle lhs, TorchTensorHandle rhs);
    TorchTensorHandle div_tensor(TorchTensorHandle lhs, TorchTensorHandle rhs);
    void div_tensor_assign(TorchTensorHandle lhs, TorchTensorHandle rhs);
    TorchTensorHandle unary_minus(TorchTensorHandle tensor_handle);

    TorchTensorHandle abs_tensor(TorchTensorHandle tensor_handle);
    void abs_tensor_assign(TorchTensorHandle tensor_handle);

    TorchTensorHandle transpose_tensor(TorchTensorHandle tensor_handle, int i, int j);
    void transpose_tensor_assign(TorchTensorHandle tensor_handle, int i, int j);

    TorchTensorHandle exp_tensor(TorchTensorHandle tensor_handle);
    void exp_tensor_assign(TorchTensorHandle tensor_handle);

    TorchTensorHandle log_tensor(TorchTensorHandle tensor_handle);
    void log_tensor_assign(TorchTensorHandle tensor_handle);

    TorchTensorHandle sum_tensor(TorchTensorHandle tensor_handle);
    void sum_tensor_assign(TorchTensorHandle tensor_handle);

    TorchTensorHandle matmul(TorchTensorHandle lhs, TorchTensorHandle rhs);
    void matmul_assign(TorchTensorHandle lhs, TorchTensorHandle rhs);
    void matmul_right_assign(TorchTensorHandle lhs, TorchTensorHandle rhs);

    TorchTensorHandle diag_embed(TorchTensorHandle diags_handle, int offset, int dim1, int dim2);

    void svd_tensor(TorchTensorHandle tensor_handle,
                    TorchTensorHandle U_handle,
                    TorchTensorHandle S_handle,
                    TorchTensorHandle V_handle);

    void symeig_tensor(TorchTensorHandle tensor_handle,
                       TorchTensorHandle S_handle,
                       TorchTensorHandle V_handle,
                       bool eigenvectors);

    bool requires_grad(TorchTensorHandle tensor_handle);
    void requires_grad_(TorchTensorHandle tensor_handle, bool status);
    TorchTensorHandle detach_from_graph(TorchTensorHandle tensor_handle);
    TorchTensorHandle autograd_tensor(TorchTensorHandle value, TorchTensorHandle variable, bool retain_graph);
    TorchTensorHandle autohess_tensor(TorchTensorHandle value, TorchTensorHandle variable);

#ifdef __cplusplus
}
#endif

#endif //CTORCH
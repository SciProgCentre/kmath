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

    TorchTensorHandle copy_from_blob_double(double *data, int *shape, int dim, int device);
    TorchTensorHandle copy_from_blob_float(float *data, int *shape, int dim, int device);
    TorchTensorHandle copy_from_blob_long(long *data, int *shape, int dim, int device);
    TorchTensorHandle copy_from_blob_int(int *data, int *shape, int dim, int device);
    TorchTensorHandle copy_tensor(TorchTensorHandle tensor_handle);
    TorchTensorHandle copy_to_device(TorchTensorHandle tensor_handle, int device);

    double get_item_double(TorchTensorHandle tensor_handle);
    float get_item_float(TorchTensorHandle tensor_handle);
    long get_item_long(TorchTensorHandle tensor_handle);
    int get_item_int(TorchTensorHandle tensor_handle);
   
    int get_dim(TorchTensorHandle tensor_handle);
    int get_numel(TorchTensorHandle tensor_handle);
    int get_shape_at(TorchTensorHandle tensor_handle, int d);
    int get_stride_at(TorchTensorHandle tensor_handle, int d);
    int get_device(TorchTensorHandle tensor_handle);

    char *tensor_to_string(TorchTensorHandle tensor_handle);
    void dispose_char(char *ptr);
    void dispose_tensor(TorchTensorHandle tensor_handle);

 
    double get_double(TorchTensorHandle tensor_handle, int* index);
    float get_float(TorchTensorHandle tensor_handle, int* index);
    long get_long(TorchTensorHandle tensor_handle, int* index);
    int get_int(TorchTensorHandle tensor_handle, int* index);
    void set_double(TorchTensorHandle tensor_handle, int* index, double value);
    void set_float(TorchTensorHandle tensor_handle, int* index, float value);
    void set_long(TorchTensorHandle tensor_handle, int* index, long value);
    void set_int(TorchTensorHandle tensor_handle, int* index, int value);


    TorchTensorHandle randn_double(int* shape, int shape_size, int device);
    TorchTensorHandle rand_double(int* shape, int shape_size, int device);
    TorchTensorHandle randn_float(int* shape, int shape_size, int device);
    TorchTensorHandle rand_float(int* shape, int shape_size, int device);

    TorchTensorHandle matmul(TorchTensorHandle lhs, TorchTensorHandle rhs);
    void matmul_assign(TorchTensorHandle lhs, TorchTensorHandle rhs);
    
#ifdef __cplusplus
}
#endif

#endif //CTORCH
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

    TorchTensorHandle copy_from_blob_double(double *data, int *shape, int dim);
    TorchTensorHandle copy_from_blob_float(float *data, int *shape, int dim);
    TorchTensorHandle copy_from_blob_long(long *data, int *shape, int dim);
    TorchTensorHandle copy_from_blob_int(int *data, int *shape, int dim);

    TorchTensorHandle copy_tensor(TorchTensorHandle tensor_handle);

    double *get_data_double(TorchTensorHandle tensor_handle);
    float *get_data_float(TorchTensorHandle tensor_handle);
    long *get_data_long(TorchTensorHandle tensor_handle);
    int *get_data_int(TorchTensorHandle tensor_handle);

    int get_numel(TorchTensorHandle tensor_handle);
    int get_dim(TorchTensorHandle tensor_handle);
    int *get_shape(TorchTensorHandle tensor_handle);
    int *get_strides(TorchTensorHandle tensor_handle);

    char *tensor_to_string(TorchTensorHandle tensor_handle);

    void dispose_int_array(int *ptr);
    void dispose_char(char *ptr);
    void dispose_tensor(TorchTensorHandle tensor_handle);

#ifdef __cplusplus
}
#endif

#endif //CTORCH
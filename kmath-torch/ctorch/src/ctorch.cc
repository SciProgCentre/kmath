#include <torch/torch.h>
#include <iostream>
#include <stdlib.h>

#include "ctorch.h"
#include "utils.hh"

int get_num_threads()
{
  return torch::get_num_threads();
}

void set_num_threads(int num_threads)
{
  torch::set_num_threads(num_threads);
}

bool cuda_is_available()
{
  return torch::cuda::is_available();
}

void set_seed(int seed)
{
  torch::manual_seed(seed);
}

TorchTensorHandle copy_from_blob_double(double *data, int *shape, int dim)
{
  return new torch::Tensor(ctorch::copy_from_blob<double>(data, shape, dim, torch::kCPU));
}
TorchTensorHandle copy_from_blob_float(float *data, int *shape, int dim)
{
  return new torch::Tensor(ctorch::copy_from_blob<float>(data, shape, dim, torch::kCPU));
}
TorchTensorHandle copy_from_blob_long(long *data, int *shape, int dim)
{
  return new torch::Tensor(ctorch::copy_from_blob<long>(data, shape, dim, torch::kCPU));
}
TorchTensorHandle copy_from_blob_int(int *data, int *shape, int dim)
{
  return new torch::Tensor(ctorch::copy_from_blob<int>(data, shape, dim, torch::kCPU));
}

TorchTensorHandle copy_from_blob_to_gpu_double(double *data, int *shape, int dim, int device)
{
  return new torch::Tensor(ctorch::copy_from_blob<double>(data, shape, dim, torch::Device(torch::kCUDA, device)));
}
TorchTensorHandle copy_from_blob_to_gpu_float(float *data, int *shape, int dim, int device)
{
  return new torch::Tensor(ctorch::copy_from_blob<float>(data, shape, dim, torch::Device(torch::kCUDA, device)));
}
TorchTensorHandle copy_from_blob_to_gpu_long(long *data, int *shape, int dim, int device)
{
  return new torch::Tensor(ctorch::copy_from_blob<long>(data, shape, dim, torch::Device(torch::kCUDA, device)));
}
TorchTensorHandle copy_from_blob_to_gpu_int(int *data, int *shape, int dim, int device)
{
  return new torch::Tensor(ctorch::copy_from_blob<int>(data, shape, dim, torch::Device(torch::kCUDA, device)));
}

TorchTensorHandle copy_tensor(TorchTensorHandle tensor_handle)
{
  return new torch::Tensor(ctorch::cast(tensor_handle).clone());
}

double *get_data_double(TorchTensorHandle tensor_handle)
{
  return ctorch::cast(tensor_handle).data_ptr<double>();
}
float *get_data_float(TorchTensorHandle tensor_handle)
{
  return ctorch::cast(tensor_handle).data_ptr<float>();
}
long *get_data_long(TorchTensorHandle tensor_handle)
{
  return ctorch::cast(tensor_handle).data_ptr<long>();
}
int *get_data_int(TorchTensorHandle tensor_handle)
{
  return ctorch::cast(tensor_handle).data_ptr<int>();
}

int get_numel(TorchTensorHandle tensor_handle)
{
  return ctorch::cast(tensor_handle).numel();
}

int get_dim(TorchTensorHandle tensor_handle)
{
  return ctorch::cast(tensor_handle).dim();
}

int *get_shape(TorchTensorHandle tensor_handle)
{
  return ctorch::to_dynamic_ints(ctorch::cast(tensor_handle).sizes());
}

int *get_strides(TorchTensorHandle tensor_handle)
{
  return ctorch::to_dynamic_ints(ctorch::cast(tensor_handle).strides());
}

char *tensor_to_string(TorchTensorHandle tensor_handle)
{
  std::stringstream bufrep;
  bufrep << ctorch::cast(tensor_handle);
  auto rep = bufrep.str();
  char *crep = (char *)malloc(rep.length() + 1);
  std::strcpy(crep, rep.c_str());
  return crep;
}

void dispose_int_array(int *ptr)
{
  free(ptr);
}

void dispose_char(char *ptr)
{
  free(ptr);
}

void dispose_tensor(TorchTensorHandle tensor_handle)
{
  delete static_cast<torch::Tensor *>(tensor_handle);
}

double get_at_offset_double(TorchTensorHandle tensor_handle, int offset)
{
  return ctorch::get_at_offset<double>(tensor_handle, offset);
}
float get_at_offset_float(TorchTensorHandle tensor_handle, int offset)
{
  return ctorch::get_at_offset<float>(tensor_handle, offset);
}
long get_at_offset_long(TorchTensorHandle tensor_handle, int offset)
{
  return ctorch::get_at_offset<long>(tensor_handle, offset);
}
int get_at_offset_int(TorchTensorHandle tensor_handle, int offset)
{
  return ctorch::get_at_offset<int>(tensor_handle, offset);
}
void set_at_offset_double(TorchTensorHandle tensor_handle, int offset, double value)
{
  ctorch::set_at_offset<double>(tensor_handle, offset, value);
}
void set_at_offset_float(TorchTensorHandle tensor_handle, int offset, float value)
{
  ctorch::set_at_offset<float>(tensor_handle, offset, value);
}
void set_at_offset_long(TorchTensorHandle tensor_handle, int offset, long value)
{
  ctorch::set_at_offset<long>(tensor_handle, offset, value);
}
void set_at_offset_int(TorchTensorHandle tensor_handle, int offset, int value)
{
  ctorch::set_at_offset<int>(tensor_handle, offset, value);
}

TorchTensorHandle copy_to_cpu(TorchTensorHandle tensor_handle)
{
  return new torch::Tensor(ctorch::cast(tensor_handle).to(torch::kCPU,false, true));
}
TorchTensorHandle copy_to_gpu(TorchTensorHandle tensor_handle, int device)
{
  return new torch::Tensor(ctorch::cast(tensor_handle).to(torch::Device(torch::kCUDA, device),false, true));
}

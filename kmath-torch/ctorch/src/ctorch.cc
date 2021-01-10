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

int get_dim(TorchTensorHandle tensor_handle)
{
  return ctorch::cast(tensor_handle).dim();
}
int get_numel(TorchTensorHandle tensor_handle)
{
  return ctorch::cast(tensor_handle).numel();
}
int get_shape_at(TorchTensorHandle tensor_handle, int d)
{
  return ctorch::cast(tensor_handle).size(d);
}
int get_stride_at(TorchTensorHandle tensor_handle, int d)
{
  return ctorch::cast(tensor_handle).stride(d);
}
int get_device(TorchTensorHandle tensor_handle)
{
  return ctorch::device_to_int(ctorch::cast(tensor_handle));
}

TorchTensorHandle copy_from_blob_double(double *data, int *shape, int dim, int device)
{
  return new torch::Tensor(ctorch::copy_from_blob<double>(data, ctorch::to_vec_int(shape, dim), ctorch::int_to_device(device)));
}
TorchTensorHandle copy_from_blob_float(float *data, int *shape, int dim, int device)
{
  return new torch::Tensor(ctorch::copy_from_blob<float>(data, ctorch::to_vec_int(shape, dim), ctorch::int_to_device(device)));
}
TorchTensorHandle copy_from_blob_long(long *data, int *shape, int dim, int device)
{
  return new torch::Tensor(ctorch::copy_from_blob<long>(data, ctorch::to_vec_int(shape, dim), ctorch::int_to_device(device)));
}
TorchTensorHandle copy_from_blob_int(int *data, int *shape, int dim, int device)
{
  return new torch::Tensor(ctorch::copy_from_blob<int>(data, ctorch::to_vec_int(shape, dim), ctorch::int_to_device(device)));
}
TorchTensorHandle copy_tensor(TorchTensorHandle tensor_handle)
{
  return new torch::Tensor(ctorch::cast(tensor_handle).clone());
}
TorchTensorHandle copy_to_device(TorchTensorHandle tensor_handle, int device)
{
  return new torch::Tensor(ctorch::cast(tensor_handle).to(ctorch::int_to_device(device), false, true));
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
void dispose_char(char *ptr)
{
  free(ptr);
}
void dispose_tensor(TorchTensorHandle tensor_handle)
{
  delete static_cast<torch::Tensor *>(tensor_handle);
}

double get_double(TorchTensorHandle tensor_handle, int *index)
{
  return ctorch::get<double>(tensor_handle, index);
}
float get_float(TorchTensorHandle tensor_handle, int *index)
{
  return ctorch::get<float>(tensor_handle, index);
}
long get_long(TorchTensorHandle tensor_handle, int *index)
{
  return ctorch::get<long>(tensor_handle, index);
}
int get_int(TorchTensorHandle tensor_handle, int *index)
{
  return ctorch::get<int>(tensor_handle, index);
}
void set_double(TorchTensorHandle tensor_handle, int *index, double value)
{
  ctorch::set<double>(tensor_handle, index, value);
}
void set_float(TorchTensorHandle tensor_handle, int *index, float value)
{
  ctorch::set<float>(tensor_handle, index, value);
}
void set_long(TorchTensorHandle tensor_handle, int *index, long value)
{
  ctorch::set<long>(tensor_handle, index, value);
}
void set_int(TorchTensorHandle tensor_handle, int *index, int value)
{
  ctorch::set<int>(tensor_handle, index, value);
}

double get_item_double(TorchTensorHandle tensor_handle)
{
  return ctorch::cast(tensor_handle).item<double>();
}
float get_item_float(TorchTensorHandle tensor_handle)
{
  return ctorch::cast(tensor_handle).item<float>();
}
long get_item_long(TorchTensorHandle tensor_handle)
{
  return ctorch::cast(tensor_handle).item<long>();
}
int get_item_int(TorchTensorHandle tensor_handle)
{
  return ctorch::cast(tensor_handle).item<int>();
}

TorchTensorHandle randn_double(int *shape, int shape_size, int device)
{
  return new torch::Tensor(ctorch::randn<double>(ctorch::to_vec_int(shape, shape_size), ctorch::int_to_device(device)));
}
TorchTensorHandle rand_double(int *shape, int shape_size, int device)
{
  return new torch::Tensor(ctorch::rand<double>(ctorch::to_vec_int(shape, shape_size), ctorch::int_to_device(device)));
}
TorchTensorHandle randn_float(int *shape, int shape_size, int device)
{
  return new torch::Tensor(ctorch::randn<float>(ctorch::to_vec_int(shape, shape_size), ctorch::int_to_device(device)));
}
TorchTensorHandle rand_float(int *shape, int shape_size, int device)
{
  return new torch::Tensor(ctorch::rand<float>(ctorch::to_vec_int(shape, shape_size), ctorch::int_to_device(device)));
}

TorchTensorHandle matmul(TorchTensorHandle lhs, TorchTensorHandle rhs)
{
  return new torch::Tensor(torch::matmul(ctorch::cast(lhs), ctorch::cast(rhs)));
}

void matmul_assign(TorchTensorHandle lhs, TorchTensorHandle rhs)
{
  ctorch::cast(lhs) = ctorch::cast(lhs).matmul(ctorch::cast(rhs));
}
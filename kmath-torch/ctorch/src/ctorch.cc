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

TorchTensorHandle empty_tensor()
{
  return new torch::Tensor;
}

TorchTensorHandle from_blob_double(double *data, int *shape, int dim, int device, bool copy)
{
  return new torch::Tensor(ctorch::from_blob<double>(data, ctorch::to_vec_int(shape, dim), ctorch::int_to_device(device), copy));
}
TorchTensorHandle from_blob_float(float *data, int *shape, int dim, int device, bool copy)
{
  return new torch::Tensor(ctorch::from_blob<float>(data, ctorch::to_vec_int(shape, dim), ctorch::int_to_device(device), copy));
}
TorchTensorHandle from_blob_long(long *data, int *shape, int dim, int device, bool copy)
{
  return new torch::Tensor(ctorch::from_blob<long>(data, ctorch::to_vec_int(shape, dim), ctorch::int_to_device(device), copy));
}
TorchTensorHandle from_blob_int(int *data, int *shape, int dim, int device, bool copy)
{
  return new torch::Tensor(ctorch::from_blob<int>(data, ctorch::to_vec_int(shape, dim), ctorch::int_to_device(device), copy));
}
TorchTensorHandle copy_tensor(TorchTensorHandle tensor_handle)
{
  return new torch::Tensor(ctorch::cast(tensor_handle).clone());
}
TorchTensorHandle copy_to_device(TorchTensorHandle tensor_handle, int device)
{
  return new torch::Tensor(ctorch::cast(tensor_handle).to(ctorch::int_to_device(device), false, true));
}
TorchTensorHandle copy_to_double(TorchTensorHandle tensor_handle)
{
  return new torch::Tensor(ctorch::cast(tensor_handle).to(ctorch::dtype<double>(), false, true));
}
TorchTensorHandle copy_to_float(TorchTensorHandle tensor_handle)
{
  return new torch::Tensor(ctorch::cast(tensor_handle).to(ctorch::dtype<float>(), false, true));
}
TorchTensorHandle copy_to_long(TorchTensorHandle tensor_handle)
{
  return new torch::Tensor(ctorch::cast(tensor_handle).to(ctorch::dtype<long>(), false, true));
}
TorchTensorHandle copy_to_int(TorchTensorHandle tensor_handle)
{
  return new torch::Tensor(ctorch::cast(tensor_handle).to(ctorch::dtype<int>(), false, true));
}
void swap_tensors(TorchTensorHandle lhs_handle, TorchTensorHandle rhs_handle)
{
  std::swap(ctorch::cast(lhs_handle), ctorch::cast(rhs_handle));
}

char *tensor_to_string(TorchTensorHandle tensor_handle)
{
  return ctorch::tensor_to_char(ctorch::cast(tensor_handle));
}
void dispose_char(char *ptr)
{
  free(ptr);
}
void dispose_tensor(TorchTensorHandle tensor_handle)
{
  delete static_cast<torch::Tensor *>(tensor_handle);
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

TorchTensorHandle rand_double(int *shape, int shape_size, int device)
{
  return new torch::Tensor(ctorch::rand<double>(ctorch::to_vec_int(shape, shape_size), ctorch::int_to_device(device)));
}
TorchTensorHandle randn_double(int *shape, int shape_size, int device)
{
  return new torch::Tensor(ctorch::randn<double>(ctorch::to_vec_int(shape, shape_size), ctorch::int_to_device(device)));
}
TorchTensorHandle rand_float(int *shape, int shape_size, int device)
{
  return new torch::Tensor(ctorch::rand<float>(ctorch::to_vec_int(shape, shape_size), ctorch::int_to_device(device)));
}
TorchTensorHandle randn_float(int *shape, int shape_size, int device)
{
  return new torch::Tensor(ctorch::randn<float>(ctorch::to_vec_int(shape, shape_size), ctorch::int_to_device(device)));
}

TorchTensorHandle randint_long(long low, long high, int *shape, int shape_size, int device)
{
  return new torch::Tensor(ctorch::randint<long>(low, high, ctorch::to_vec_int(shape, shape_size), ctorch::int_to_device(device)));
}
TorchTensorHandle randint_int(int low, int high, int *shape, int shape_size, int device)
{
  return new torch::Tensor(ctorch::randint<int>(low, high, ctorch::to_vec_int(shape, shape_size), ctorch::int_to_device(device)));
}

TorchTensorHandle rand_like(TorchTensorHandle tensor_handle)
{
  return new torch::Tensor(torch::rand_like(ctorch::cast(tensor_handle)));
}
void rand_like_assign(TorchTensorHandle tensor_handle)
{
  ctorch::cast(tensor_handle) = torch::rand_like(ctorch::cast(tensor_handle));
}
TorchTensorHandle randn_like(TorchTensorHandle tensor_handle)
{
  return new torch::Tensor(torch::randn_like(ctorch::cast(tensor_handle)));
}
void randn_like_assign(TorchTensorHandle tensor_handle)
{
  ctorch::cast(tensor_handle) = torch::randn_like(ctorch::cast(tensor_handle));
}
TorchTensorHandle randint_long_like(TorchTensorHandle tensor_handle, long low, long high)
{
  return new torch::Tensor(torch::randint_like(ctorch::cast(tensor_handle), low, high));
}
void randint_long_like_assign(TorchTensorHandle tensor_handle, long low, long high)
{
  ctorch::cast(tensor_handle) = torch::randint_like(ctorch::cast(tensor_handle), low, high);
}
TorchTensorHandle randint_int_like(TorchTensorHandle tensor_handle, int low, int high)
{
  return new torch::Tensor(torch::randint_like(ctorch::cast(tensor_handle), low, high));
}
void randint_int_like_assign(TorchTensorHandle tensor_handle, int low, int high)
{
  ctorch::cast(tensor_handle) = torch::randint_like(ctorch::cast(tensor_handle), low, high);
}

TorchTensorHandle full_double(double value, int *shape, int shape_size, int device)
{
  return new torch::Tensor(ctorch::full<double>(value, ctorch::to_vec_int(shape, shape_size), ctorch::int_to_device(device)));
}
TorchTensorHandle full_float(float value, int *shape, int shape_size, int device)
{
  return new torch::Tensor(ctorch::full<float>(value, ctorch::to_vec_int(shape, shape_size), ctorch::int_to_device(device)));
}
TorchTensorHandle full_long(long value, int *shape, int shape_size, int device)
{
  return new torch::Tensor(ctorch::full<long>(value, ctorch::to_vec_int(shape, shape_size), ctorch::int_to_device(device)));
}
TorchTensorHandle full_int(int value, int *shape, int shape_size, int device)
{
  return new torch::Tensor(ctorch::full<int>(value, ctorch::to_vec_int(shape, shape_size), ctorch::int_to_device(device)));
}

TorchTensorHandle plus_double(double value, TorchTensorHandle other)
{
  return new torch::Tensor(ctorch::cast(other) + value);
}
TorchTensorHandle plus_float(float value, TorchTensorHandle other)
{
  return new torch::Tensor(ctorch::cast(other) + value);
}
TorchTensorHandle plus_long(long value, TorchTensorHandle other)
{
  return new torch::Tensor(ctorch::cast(other) + value);
}
TorchTensorHandle plus_int(int value, TorchTensorHandle other)
{
  return new torch::Tensor(ctorch::cast(other) + value);
}
void plus_double_assign(double value, TorchTensorHandle other)
{
  ctorch::cast(other) += value;
}
void plus_float_assign(float value, TorchTensorHandle other)
{
  ctorch::cast(other) += value;
}
void plus_long_assign(long value, TorchTensorHandle other)
{
  ctorch::cast(other) += value;
}
void plus_int_assign(int value, TorchTensorHandle other)
{
  ctorch::cast(other) += value;
}

TorchTensorHandle times_double(double value, TorchTensorHandle other)
{
  return new torch::Tensor(value * ctorch::cast(other));
}
TorchTensorHandle times_float(float value, TorchTensorHandle other)
{
  return new torch::Tensor(value * ctorch::cast(other));
}
TorchTensorHandle times_long(long value, TorchTensorHandle other)
{
  return new torch::Tensor(value * ctorch::cast(other));
}
TorchTensorHandle times_int(int value, TorchTensorHandle other)
{
  return new torch::Tensor(value * ctorch::cast(other));
}
void times_double_assign(double value, TorchTensorHandle other)
{
  ctorch::cast(other) *= value;
}
void times_float_assign(float value, TorchTensorHandle other)
{
  ctorch::cast(other) *= value;
}
void times_long_assign(long value, TorchTensorHandle other)
{
  ctorch::cast(other) *= value;
}
void times_int_assign(int value, TorchTensorHandle other)
{
  ctorch::cast(other) *= value;
}

TorchTensorHandle plus_tensor(TorchTensorHandle lhs, TorchTensorHandle rhs)
{
  return new torch::Tensor(ctorch::cast(lhs) + ctorch::cast(rhs));
}
void plus_tensor_assign(TorchTensorHandle lhs, TorchTensorHandle rhs)
{
  ctorch::cast(lhs) += ctorch::cast(rhs);
}
TorchTensorHandle minus_tensor(TorchTensorHandle lhs, TorchTensorHandle rhs)
{
  return new torch::Tensor(ctorch::cast(lhs) - ctorch::cast(rhs));
}
void minus_tensor_assign(TorchTensorHandle lhs, TorchTensorHandle rhs)
{
  ctorch::cast(lhs) -= ctorch::cast(rhs);
}
TorchTensorHandle times_tensor(TorchTensorHandle lhs, TorchTensorHandle rhs)
{
  return new torch::Tensor(ctorch::cast(lhs) * ctorch::cast(rhs));
}
void times_tensor_assign(TorchTensorHandle lhs, TorchTensorHandle rhs)
{
  ctorch::cast(lhs) *= ctorch::cast(rhs);
}
TorchTensorHandle div_tensor(TorchTensorHandle lhs, TorchTensorHandle rhs)
{
  return new torch::Tensor(ctorch::cast(lhs) / ctorch::cast(rhs));
}
void div_tensor_assign(TorchTensorHandle lhs, TorchTensorHandle rhs)
{
  ctorch::cast(lhs) /= ctorch::cast(rhs);
}
TorchTensorHandle unary_minus(TorchTensorHandle tensor_handle)
{
  return new torch::Tensor(-ctorch::cast(tensor_handle));
}

TorchTensorHandle abs_tensor(TorchTensorHandle tensor_handle)
{
  return new torch::Tensor(ctorch::cast(tensor_handle).abs());
}
void abs_tensor_assign(TorchTensorHandle tensor_handle)
{
  ctorch::cast(tensor_handle) = ctorch::cast(tensor_handle).abs();
}

TorchTensorHandle transpose_tensor(TorchTensorHandle tensor_handle, int i, int j)
{
  return new torch::Tensor(ctorch::cast(tensor_handle).transpose(i, j));
}
void transpose_tensor_assign(TorchTensorHandle tensor_handle, int i, int j)
{
  ctorch::cast(tensor_handle) = ctorch::cast(tensor_handle).transpose(i, j);
}

TorchTensorHandle exp_tensor(TorchTensorHandle tensor_handle)
{
  return new torch::Tensor(ctorch::cast(tensor_handle).exp());
}
void exp_tensor_assign(TorchTensorHandle tensor_handle)
{
  ctorch::cast(tensor_handle) = ctorch::cast(tensor_handle).exp();
}

TorchTensorHandle log_tensor(TorchTensorHandle tensor_handle)
{
  return new torch::Tensor(ctorch::cast(tensor_handle).log());
}
void log_tensor_assign(TorchTensorHandle tensor_handle)
{
  ctorch::cast(tensor_handle) = ctorch::cast(tensor_handle).log();
}

TorchTensorHandle sum_tensor(TorchTensorHandle tensor_handle)
{
  return new torch::Tensor(ctorch::cast(tensor_handle).sum());
}
void sum_tensor_assign(TorchTensorHandle tensor_handle)
{
  ctorch::cast(tensor_handle) = ctorch::cast(tensor_handle).sum();
}

TorchTensorHandle matmul(TorchTensorHandle lhs, TorchTensorHandle rhs)
{
  return new torch::Tensor(torch::matmul(ctorch::cast(lhs), ctorch::cast(rhs)));
}
void matmul_assign(TorchTensorHandle lhs, TorchTensorHandle rhs)
{
  ctorch::cast(lhs) = ctorch::cast(lhs).matmul(ctorch::cast(rhs));
}
void matmul_right_assign(TorchTensorHandle lhs, TorchTensorHandle rhs)
{
  ctorch::cast(rhs) = ctorch::cast(lhs).matmul(ctorch::cast(rhs));
}

TorchTensorHandle diag_embed(TorchTensorHandle diags_handle, int offset, int dim1, int dim2)
{
  return new torch::Tensor(torch::diag_embed(ctorch::cast(diags_handle), offset, dim1, dim2));
}

void svd_tensor(TorchTensorHandle tensor_handle,
                TorchTensorHandle U_handle,
                TorchTensorHandle S_handle,
                TorchTensorHandle V_handle)
{
  auto [U, S, V] = torch::svd(ctorch::cast(tensor_handle));
  ctorch::cast(U_handle) = U;
  ctorch::cast(S_handle) = S;
  ctorch::cast(V_handle) = V;
}

void symeig_tensor(TorchTensorHandle tensor_handle,
                   TorchTensorHandle S_handle,
                   TorchTensorHandle V_handle,
                   bool eigenvectors)
{
  auto [S, V] = torch::symeig(ctorch::cast(tensor_handle), eigenvectors);
  ctorch::cast(S_handle) = S;
  ctorch::cast(V_handle) = V;
}

bool requires_grad(TorchTensorHandle tensor_handle)
{
  return ctorch::cast(tensor_handle).requires_grad();
}
void requires_grad_(TorchTensorHandle tensor_handle, bool status)
{
  ctorch::cast(tensor_handle).requires_grad_(status);
}
TorchTensorHandle detach_from_graph(TorchTensorHandle tensor_handle)
{
  return new torch::Tensor(ctorch::cast(tensor_handle).detach());
}
TorchTensorHandle autograd_tensor(TorchTensorHandle value, TorchTensorHandle variable, bool retain_graph)
{
  return new torch::Tensor(torch::autograd::grad({ctorch::cast(value)}, {ctorch::cast(variable)}, {}, retain_graph)[0]);
}
TorchTensorHandle autohess_tensor(TorchTensorHandle value, TorchTensorHandle variable)
{
  return new torch::Tensor(ctorch::hessian(ctorch::cast(value), ctorch::cast(variable)));
}

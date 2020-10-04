/* statistics/gsl_statistics_long.h
 * 
 * Copyright (C) 1996, 1997, 1998, 1999, 2000, 2007 Jim Davies, Brian Gough
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or (at
 * your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */

#ifndef __GSL_STATISTICS_LONG_H__
#define __GSL_STATISTICS_LONG_H__

#include <stddef.h>
#include <stdlib.h>

#undef __BEGIN_DECLS
#undef __END_DECLS
#ifdef __cplusplus
# define __BEGIN_DECLS extern "C" {
# define __END_DECLS }
#else
# define __BEGIN_DECLS /* empty */
# define __END_DECLS /* empty */
#endif

__BEGIN_DECLS

double gsl_stats_long_mean (const long data[], const size_t stride, const size_t n);
double gsl_stats_long_variance (const long data[], const size_t stride, const size_t n);
double gsl_stats_long_sd (const long data[], const size_t stride, const size_t n);
double gsl_stats_long_variance_with_fixed_mean (const long data[], const size_t stride, const size_t n, const double mean);
double gsl_stats_long_sd_with_fixed_mean (const long data[], const size_t stride, const size_t n, const double mean);
double gsl_stats_long_tss (const long data[], const size_t stride, const size_t n);
double gsl_stats_long_tss_m (const long data[], const size_t stride, const size_t n, const double mean);

double gsl_stats_long_absdev (const long data[], const size_t stride, const size_t n);
double gsl_stats_long_skew (const long data[], const size_t stride, const size_t n);
double gsl_stats_long_kurtosis (const long data[], const size_t stride, const size_t n);
double gsl_stats_long_lag1_autocorrelation (const long data[], const size_t stride, const size_t n);

double gsl_stats_long_covariance (const long data1[], const size_t stride1,const long data2[], const size_t stride2, const size_t n);
double gsl_stats_long_correlation (const long data1[], const size_t stride1,const long data2[], const size_t stride2, const size_t n);
double gsl_stats_long_spearman (const long data1[], const size_t stride1, const long data2[], const size_t stride2, const size_t n, double work[]);

double gsl_stats_long_variance_m (const long data[], const size_t stride, const size_t n, const double mean);
double gsl_stats_long_sd_m (const long data[], const size_t stride, const size_t n, const double mean);
double gsl_stats_long_absdev_m (const long data[], const size_t stride, const size_t n, const double mean);
double gsl_stats_long_skew_m_sd (const long data[], const size_t stride, const size_t n, const double mean, const double sd);
double gsl_stats_long_kurtosis_m_sd (const long data[], const size_t stride, const size_t n, const double mean, const double sd);
double gsl_stats_long_lag1_autocorrelation_m (const long data[], const size_t stride, const size_t n, const double mean);

double gsl_stats_long_covariance_m (const long data1[], const size_t stride1,const long data2[], const size_t stride2, const size_t n, const double mean1, const double mean2);


double gsl_stats_long_pvariance (const long data1[], const size_t stride1, const size_t n1, const long data2[], const size_t stride2, const size_t n2);
double gsl_stats_long_ttest (const long data1[], const size_t stride1, const size_t n1, const long data2[], const size_t stride2, const size_t n2);

long gsl_stats_long_max (const long data[], const size_t stride, const size_t n);
long gsl_stats_long_min (const long data[], const size_t stride, const size_t n);
void gsl_stats_long_minmax (long * min, long * max, const long data[], const size_t stride, const size_t n);

size_t gsl_stats_long_max_index (const long data[], const size_t stride, const size_t n);
size_t gsl_stats_long_min_index (const long data[], const size_t stride, const size_t n);
void gsl_stats_long_minmax_index (size_t * min_index, size_t * max_index, const long data[], const size_t stride, const size_t n);

long gsl_stats_long_select(long data[], const size_t stride, const size_t n, const size_t k);

double gsl_stats_long_median_from_sorted_data (const long sorted_data[], const size_t stride, const size_t n) ;
double gsl_stats_long_median (long sorted_data[], const size_t stride, const size_t n);
double gsl_stats_long_quantile_from_sorted_data (const long sorted_data[], const size_t stride, const size_t n, const double f) ;

double gsl_stats_long_trmean_from_sorted_data (const double trim, const long sorted_data[], const size_t stride, const size_t n) ;
double gsl_stats_long_gastwirth_from_sorted_data (const long sorted_data[], const size_t stride, const size_t n) ;

double gsl_stats_long_mad0(const long data[], const size_t stride, const size_t n, double work[]);
double gsl_stats_long_mad(const long data[], const size_t stride, const size_t n, double work[]);

long gsl_stats_long_Sn0_from_sorted_data (const long sorted_data[], const size_t stride, const size_t n, long work[]) ;
double gsl_stats_long_Sn_from_sorted_data (const long sorted_data[], const size_t stride, const size_t n, long work[]) ;

long gsl_stats_long_Qn0_from_sorted_data (const long sorted_data[], const size_t stride, const size_t n, long work[], int work_int[]) ;
double gsl_stats_long_Qn_from_sorted_data (const long sorted_data[], const size_t stride, const size_t n, long work[], int work_int[]) ;

__END_DECLS

#endif /* __GSL_STATISTICS_LONG_H__ */

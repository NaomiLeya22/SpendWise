package com.example.spendwise.network;

import com.example.spendwise.dto.ApiEnvelope;
import com.example.spendwise.dto.AuthDtos;
import com.example.spendwise.dto.BudgetDto;
import com.example.spendwise.dto.TransactionDto;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;

public interface ExpenseApi {

    @POST("auth/register")
    Call<ApiEnvelope<Map<String, Object>>> register(
            @Body AuthDtos.RegisterRequest request
    );

    @POST("auth/login")
    Call<ApiEnvelope<AuthDtos.AuthData>> login(
            @Body AuthDtos.LoginRequest request
    );

    @GET("auth/me")
    Call<ApiEnvelope<AuthDtos.UserDto>> me();

    @POST("auth/logout")
    Call<ApiEnvelope<Map<String, Object>>> logout();

    @GET("transactions")
    Call<ApiEnvelope<List<TransactionDto>>> getTransactions();

    @POST("transactions")
    Call<ApiEnvelope<TransactionDto>> createTransaction(
            @Body TransactionDto transaction
    );

    @PUT("transactions")
    Call<ApiEnvelope<TransactionDto>> updateTransaction(
            @Query("id") long id,
            @Body TransactionDto transaction
    );

    @DELETE("transactions")
    Call<ApiEnvelope<Map<String, Object>>> deleteTransaction(
            @Query("id") long id
    );

    @GET("budgets")
    Call<ApiEnvelope<List<BudgetDto>>> getBudgets(
            @Query("month") String month
    );

    @POST("budgets")
    Call<ApiEnvelope<BudgetDto>> saveBudget(
            @Body BudgetDto budget
    );
}

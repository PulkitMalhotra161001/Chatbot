package com.example.chatbot;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.chatbot.databinding.ActivityMainBinding;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    private final String BOT_KEY = "bot", USER_KEY = "user";
    ArrayList<ChatsModel> chatsModelArrayList;
    private ChatRvAdapter chatRvAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.idEdtMessage.requestFocus();
        chatsModelArrayList = new ArrayList<>();
        chatRvAdapter = new ChatRvAdapter(chatsModelArrayList,this);

        //vertical orientation
        LinearLayoutManager manager = new LinearLayoutManager(this);
        binding.idRVChats.setLayoutManager(manager);

        binding.idRVChats.setAdapter(chatRvAdapter);

        binding.idFABSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(binding.idEdtMessage.getText().toString().isEmpty()){
                    Toast.makeText(MainActivity.this, "Please enter some message", Toast.LENGTH_SHORT).show();
                }else{
                    getResponse(binding.idEdtMessage.getText().toString());
                    binding.idEdtMessage.setText("");
                }
            }


        });



    }

    private void getResponse(String message) {
        chatsModelArrayList.add(new ChatsModel(message,USER_KEY));
        chatRvAdapter.notifyDataSetChanged();

        String url = "http://api.brainshop.ai/get?bid=172753&key=KeSoP2pQcZjdBgZb&uid=[uid]&msg="+message;
        String BASE_URL = "http://api.brainshop.ai/";
        Retrofit retrofit =new Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        RetrofitAPI retrofitAPI = retrofit.create(RetrofitAPI.class);
        Call<MsgModel> call = retrofitAPI.getMessage(url);
        call.enqueue(new Callback<MsgModel>() {
            @Override
            public void onResponse(Call<MsgModel> call, Response<MsgModel> response) {
                if(response.isSuccessful()){
                    MsgModel model = response.body();
                    chatsModelArrayList.add(new ChatsModel(model.getCnt(),BOT_KEY));
                    chatRvAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<MsgModel> call, Throwable t) {
                chatsModelArrayList.add(new ChatsModel("Please revert your questions",BOT_KEY));
                chatRvAdapter.notifyDataSetChanged();

            }
        });
    }

}
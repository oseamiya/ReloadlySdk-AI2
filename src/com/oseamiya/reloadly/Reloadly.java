package com.oseamiya.reloadly;

import android.content.Context;
import com.google.appinventor.components.annotations.SimpleEvent;
import com.google.appinventor.components.annotations.SimpleFunction;
import com.google.appinventor.components.annotations.SimpleProperty;
import com.google.appinventor.components.runtime.AndroidNonvisibleComponent;
import com.google.appinventor.components.runtime.ComponentContainer;
import com.google.appinventor.components.runtime.EventDispatcher;
import com.google.appinventor.components.runtime.util.YailList;
import com.neovisionaries.i18n.CountryCode;
import software.reloadly.sdk.airtime.client.AirtimeAPI;
import software.reloadly.sdk.airtime.dto.Phone;
import software.reloadly.sdk.airtime.dto.request.EmailTopupRequest;
import software.reloadly.sdk.airtime.dto.request.PhoneTopupRequest;
import software.reloadly.sdk.airtime.dto.response.*;
import software.reloadly.sdk.core.dto.response.Page;
import software.reloadly.sdk.core.enums.Environment;
import software.reloadly.sdk.core.exception.APIException;
import software.reloadly.sdk.core.exception.ReloadlyException;
import software.reloadly.sdk.core.internal.dto.request.interfaces.Request;

/*
import com.oseamiya.lib.airtime.client.AirtimeAPI;
import com.oseamiya.lib.airtime.dto.Phone;
import com.oseamiya.lib.airtime.dto.request.EmailTopupRequest;
import com.oseamiya.lib.airtime.dto.request.PhoneTopupRequest;
import com.oseamiya.lib.airtime.dto.response.*;
import com.oseamiya.lib.core.dto.response.Page;
import com.oseamiya.lib.core.enums.Environment;
import com.oseamiya.lib.core.exception.APIException;
import com.oseamiya.lib.core.exception.ReloadlyException;
import com.oseamiya.lib.core.internal.dto.request.interfaces.Request;
*/

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

public class Reloadly extends AndroidNonvisibleComponent {
private Context context;
private String clientId;
private String clientSecret;
private AirtimeAPI airtimeAPI;

  public Reloadly(ComponentContainer container) {
    super(container.$form());
    this.clientId = "";
    this.clientSecret = "";
  }

  @SimpleEvent
  public void GotError(String message){
      EventDispatcher.dispatchEvent(this , "GotError" , message);
  }

  @SimpleProperty
  public void ClientId(String value){
      this.clientId = value;
  }
  @SimpleProperty
  public void ClientSecret(String value){
      this.clientSecret = value;
  }
   @SimpleFunction
  public void Init(){
       try {
           Request<AccountBalanceInfo> request = AirtimeAPI.builder()
                   .clientId(this.clientId)
                   .clientSecret(this.clientSecret)
                   .environment(Environment.SANDBOX)
                   .build()
                   .accounts()
                   .getBalance();
            airtimeAPI = AirtimeAPI.builder()
                   .clientId(this.clientId)
                   .clientSecret(this.clientSecret)
                   .environment(Environment.SANDBOX)
                   .build();
           AccountBalanceInfo accountBalanceInfo = null;

           try{
               accountBalanceInfo = request.execute();
           } catch (APIException e) {
               //api error
               GotError(e.getMessage());
           } catch (ReloadlyException e) {
               // request error
               GotError(e.getMessage());
           } catch (Exception e){
               // all other errors
               GotError(e.getMessage());
           }
       } catch (ReloadlyException e) {
           e.printStackTrace();
       }
   }


   @SimpleFunction
    public YailList GetCountriesList(){
      Request<List<Country>> request = null;
       try {
           request = airtimeAPI.countries().list();
       } catch (ReloadlyException e) {
           e.printStackTrace();
           GotError("Api Error");
       }
       List<Country> countries = null;
       try{
           assert request != null;
           countries = request.execute();
       } catch (APIException e){
           GotError("API ERROR");
       } catch (ReloadlyException e){
           GotError("REQUEST ERROR");
       } catch (Exception e){
           GotError(e.getMessage());
       }
       String[] strings = new String[countries.size()];
      for(int i=0 ; i<= countries.size() ; i++){
          strings[i] = countries.get(i).getName();
      }
      return YailList.makeList(strings);
   }

   @SimpleFunction
    public YailList GetDiscountsList(){
      Request<Page<Discount>> request = null;
       try {
           request = airtimeAPI.discounts().list();
       } catch (ReloadlyException e) {
           e.printStackTrace();
           GotError("API ERROR");
       }

       Page<Discount> discountPage =  null;
       try{
           assert request != null;
           discountPage = request.execute();
       }catch (ReloadlyException e){
           GotError("REQUEST ERROR");
       } catch (Exception e){
           GotError(e.getMessage());
       }

       String[] strings = new String[discountPage.getSize()];
       for(int i=0 ; i<= discountPage.getSize() ; i++){
           strings[i] = discountPage.getContent().get(i).toString();
       }
       final YailList yailList = YailList.makeList(strings);
       return yailList;
  }

  @SimpleFunction
    public YailList GetOperatorLists(){
      Request<Page<Operator>> request = null;
      try {
          request = airtimeAPI.operators().list();
      } catch (ReloadlyException e) {
          e.printStackTrace();
          GotError("API ERROR");
      }
      Page<Operator> operatorPage = null;
      try{
          assert request != null;
          operatorPage = request.execute();
      } catch (APIException e){
          GotError("API ERROR");
      } catch (ReloadlyException e){
          GotError("REQUEST ERROR");
      } catch (Exception e){
          GotError(e.getMessage());
      }

      String[] strings = new String[operatorPage.getSize()];
      for(int i=0 ; i<= operatorPage.getSize() ; i++){
          strings[i] = operatorPage.getContent().get(i).getName();
      }
      return YailList.makeList(strings);
  }

  @SimpleFunction
    public YailList GetOperatorsOfIndia() {
      Request<List<Operator>> request = null;
      try {
          request = airtimeAPI.operators().listByCountryCode(CountryCode.IN);
      } catch (ReloadlyException e) {
          e.printStackTrace();
          GotError("API ERROR");
      }
      List<Operator> operators = null;
      try {
          operators = request.execute();
      } catch (APIException e) {
          GotError("API ERROR");
      } catch (ReloadlyException e) {
          GotError("REQUEST ERROR");
      } catch (Exception e) {
          GotError(e.getMessage());
      }
      String[] strings = new String[operators.size()];
      for(int i=0 ; i<=operators.size() ; i++){
          strings[i] = operators.get(i).getName();
      }
      return YailList.makeList(strings);
  }

  @SimpleEvent
  public void GotFxRate(float fxRate , String operatorName){
      EventDispatcher.dispatchEvent(this , "GotFxRate" , new Object[]{fxRate , operatorName});
  }
  @SimpleFunction
    public void GetFxRate(String operatorId , double amount){
      Request<OperatorFxRate> request = null;
      try {
          request = airtimeAPI.operators().calculateFxRate(Long.valueOf(operatorId), amount);
      } catch (ReloadlyException e) {
          e.printStackTrace();
          GotError("API ERROR");
      }
      OperatorFxRate operatorFxRate = null;
      try{
          operatorFxRate = request.execute();
      } catch (APIException e){
          GotError("API ERROR");
      }catch (ReloadlyException e) {
          GotError("REQUEST ERROR");
      } catch (Exception e) {
          GotError(e.getMessage());
      }
      GotFxRate(operatorFxRate.getFxRate() , operatorFxRate.getOperatorName());
  }

  @SimpleFunction
    public YailList GetPromotionsListOfIndia(){
      Request<List<Promotion>> request =  null;
      try {
          request = airtimeAPI.promotions().getByCountryCode(CountryCode.IN);
      } catch (ReloadlyException e) {
          e.printStackTrace();
          GotError("API ERROR");
      }
      List<Promotion> promotions =  null;
      try{
          promotions = request.execute();
      } catch (APIException e){
          GotError("API ERROR");
      }catch (ReloadlyException e) {
          GotError("REQUEST ERROR");
      } catch (Exception e) {
          GotError(e.getMessage());
      }
      String[] strings = new String[promotions.size()];
      for(int i=0 ; i<=promotions.size() ; i++){
          strings[i] = promotions.get(i).toString();
      }
      return YailList.makeList(strings);

  }
  @SimpleEvent
  public void GotTransaction(String transactionMessage){
      EventDispatcher.dispatchEvent(this , "GotTransaction", transactionMessage);
  }
  @SimpleFunction
    public void SendTopupToPhone(String operatorId , double amount , String senderPhoneNumber , String senderCountryCode , String reciptentPhoneNumber , String reciptentCountryCode){
      String internalRefrenceId = UUID.randomUUID().toString();

      PhoneTopupRequest topupRequest = PhoneTopupRequest.builder()
              .amount(amount)
              .operatorId(Long.valueOf(operatorId))
              .customIdentifier(internalRefrenceId)
              .senderPhone(new Phone(senderPhoneNumber , CountryCode.getByCode(senderCountryCode , false)))
              .recipientPhone(new Phone(reciptentPhoneNumber , CountryCode.getByCode(reciptentCountryCode , false)))
              .build();
      Request<TopupTransaction> request = null;
      try {
          request = airtimeAPI.topups().send(topupRequest);
      } catch (ReloadlyException e) {
          e.printStackTrace();
          GotError("API ERROR");
      }
      TopupTransaction transaction =  null;
      try{
          transaction = request.execute();
      } catch (APIException e){
          e.printStackTrace();
          GotError("API ERROR");
      } catch (ReloadlyException e){
          e.printStackTrace();
          GotError("REQUEST ERROR");
      } catch (Exception e){
          GotError(e.getMessage());
      }
      GotTransaction(transaction.toString());
  }

  @SimpleFunction
    public void SendTopupToPhoneInLocalAmount(String operatorId , double amount ,String senderPhoneNumber , String senderCountryCode , String reciptentPhoneNumber , String reciptentCountryCode ){
      String internalRefrenceId = UUID.randomUUID().toString();

      PhoneTopupRequest topupRequest = PhoneTopupRequest.builder()
              .amount(amount)
              .operatorId(Long.valueOf(operatorId))
              .useLocalAmount(true)
              .customIdentifier(internalRefrenceId)
              .senderPhone(new Phone(senderPhoneNumber , CountryCode.getByCode(senderCountryCode , false)))
              .recipientPhone(new Phone(reciptentPhoneNumber , CountryCode.getByCode(reciptentCountryCode , false)))
              .build();
      Request<TopupTransaction> request = null;
      try {
          request = airtimeAPI.topups().send(topupRequest);
      } catch (ReloadlyException e) {
          e.printStackTrace();
          GotError("API ERROR");
      }
      TopupTransaction transaction =  null;
      try{
          transaction = request.execute();
      } catch (APIException e){
          e.printStackTrace();
          GotError("API ERROR");
      } catch (ReloadlyException e){
          e.printStackTrace();
          GotError("REQUEST ERROR");
      } catch (Exception e){
          GotError(e.getMessage());
      }
      GotTransaction(transaction.toString());
  }

  @SimpleFunction
    public void SendNautaCubaTopups(String operatorId , double amount , String senderPhone , String senderCountryCode,  String reciptentEmail){
      String internalRefrencedId = UUID.randomUUID().toString();

      EmailTopupRequest topupRequest = EmailTopupRequest.builder()
              .amount(amount)
              .operatorId(Long.valueOf(operatorId))
              .customIdentifier(internalRefrencedId)
              .senderPhone(new Phone(senderPhone , CountryCode.getByCode(senderCountryCode , false)))
              .recipientEmail(reciptentEmail)
              .build();

      Request<TopupTransaction> request = null;
      try {
          request = airtimeAPI.topups().send(topupRequest);
      } catch (ReloadlyException e) {
          e.printStackTrace();
          GotError("API ERROR");
      }
      TopupTransaction transaction =  null;
      try{
          transaction = request.execute();
      } catch (APIException e){
          e.printStackTrace();
          GotError("API ERROR");
      } catch (ReloadlyException e){
          e.printStackTrace();
          GotError("REQUEST ERROR");
      } catch (Exception e){
          GotError(e.getMessage());
      }
      GotTransaction(transaction.toString());

  }

}

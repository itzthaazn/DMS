/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testsoap;

import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;

/**
 *
 * @author Matt
 */
@WebService(serviceName = "HelloWorldFromSoap")
public class HelloWorldFromSoap {

    
    private String greeting;
    
    public HelloWorldFromSoap(){
        greeting = " Hello from soap";
    }
    /**
     * This is a sample web service operation
     */
    
    
    
    
    @WebMethod(operationName = "hello")
    public String hello(@WebParam(name = "name") String txt) {
        return "Hello " + txt + " !";
    }
    
        /**
     * Web service operation
     */
    @WebMethod(operationName = "setGreeting")
    public void setGreeting(@WebParam(name = "greeting") String greeting) {
        //TODO write your implementation code here:
        this.greeting = greeting;
    }
    

    /**
     * Web service operation
     */
    @WebMethod(operationName = "getGreeting")
    public String getGreeting(@WebParam(name = "name") String name) {
        //TODO write your implementation code here:
        return "hi " + name + greeting;
        
    }

}

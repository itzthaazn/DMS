/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hellosoapclient;
// new web service client and put WSDL URK


/**
 *
 * @author Matt
 */
public class HelloSoapClient {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic 
        
        testsoap.HelloWorldFromSoap_Service service = new testsoap.HelloWorldFromSoap_Service();
        testsoap.HelloWorldFromSoap port = service.getHelloWorldFromSoapPort();
        System.out.println(port.getGreeting("Matt"));
        port.setGreeting(" Kia Ora ");
        System.out.println(port.getGreeting("Matt"));
    }


    
}

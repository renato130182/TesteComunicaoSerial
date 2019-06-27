/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Protocolo;

/**
 *
 * @author renato.soares
 */
public class Zumbach_USYS10 {
    private String orderNumber;
    private String productNumber;
    private String periodNumber;
    private String producedLenght;
    private String events;
    private String average;
    private String maximum;
    private String minimum;
    private String standardDeviation;
    private String maxMinRange;
    private String cpk;    
    private String leituraSerial;

    public void TratarDadosSerial(){    
        String linha = leituraSerial.toLowerCase();
        String[] aux;
        if (linha.contains("Order number".toLowerCase())){
            aux=leituraSerial.split("\t");
            orderNumber=aux[aux.length-1];
        }
        else if (linha.contains("Product number".toLowerCase())){
            productNumber=linha;
        }
        else if (linha.contains("Period number".toLowerCase())){
            periodNumber=linha;
        }
        else if (linha.contains("Produced length".toLowerCase())){
            producedLenght=linha;
        }
        else if (linha.contains("Events".toLowerCase())){
            events=linha;
        }
        else if (linha.contains("Average".toLowerCase())){
            average=linha;
        }
        else if (linha.contains("Maximum".toLowerCase())){
            maximum=linha;
        }
        else if (linha.contains("Minimum".toLowerCase())){
            minimum=linha;
        }
        else if (linha.contains("Standard deviation".toLowerCase())){
            standardDeviation=linha;
        }
        else if (linha.contains("Max-Min Range".toLowerCase())){
            maxMinRange=linha;
        }
        else if (linha.contains("CpK".toLowerCase())){
            cpk=linha;
        }
    }    
    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getProductNumber() {
        return productNumber;
    }

    public void setProductNumber(String productNumber) {
        this.productNumber = productNumber;
    }

    public String getPeriodNumber() {
        return periodNumber;
    }

    public void setPeriodNumber(String periodNumber) {
        this.periodNumber = periodNumber;
    }

    public String getProducedLenght() {
        return producedLenght;
    }

    public void setProducedLenght(String producedLenght) {
        this.producedLenght = producedLenght;
    }

    public String getEvents() {
        return events;
    }

    public void setEvents(String events) {
        this.events = events;
    }

    public String getAverage() {
        return average;
    }

    public void setAverage(String average) {
        this.average = average;
    }

    public String getMaximum() {
        return maximum;
    }

    public void setMaximum(String maximum) {
        this.maximum = maximum;
    }

    public String getMinimum() {
        return minimum;
    }

    public void setMinimum(String minimum) {
        this.minimum = minimum;
    }

    public String getStandardDeviation() {
        return standardDeviation;
    }

    public void setStandardDeviation(String standardDeviation) {
        this.standardDeviation = standardDeviation;
    }

    public String getMaxMinRange() {
        return maxMinRange;
    }

    public void setMaxMinRange(String maxMinRange) {
        this.maxMinRange = maxMinRange;
    }

    public String getCpk() {
        return cpk;
    }

    public void setCpk(String cpk) {
        this.cpk = cpk;
    }
    
    public void setLeituraSerial(String leituraSerial) {
        this.leituraSerial = leituraSerial;
        this.TratarDadosSerial();
    }
}

package model;

public enum Cores {
    PRETO("00"), MARROM("01"), VERMELHO("02"), YY("03"), AMARELO("04"), VERDE("05"), 
    AZUL("06"), XX("07"), CINZA("08"), BRANCO("09"), VERDE_AMARELO("54");
    
    String cor;
    private Cores(String cor){
        this.cor=cor;
    }
    public String getCor(){
        return cor;
    }
}

package ar.uba.fi.splitapp;

public class SettlementPayment {

    public Participant p1;
    public Participant p2;
    float result;

    public SettlementPayment(Participant p1, Participant p2, float result) {
        this.p1 = p1;
        this.p2 = p2;
        this.result = result;
    }
}

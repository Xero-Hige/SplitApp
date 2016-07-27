package ar.uba.fi.splitapp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ExpensesCalculator{
	float media = 0;
	Participant max = null;
	Participant min = null;

	public class Participant {
		public float payed;
	}		// TODO
	public class SettlementPayment {
		public SettlementPayment(Participant p1, Participant p2, float result) {}
	} // TODO

	public ExpensesCalculator(){}

	public List<SettlementPayment> calculateExpenses(List<Participant> participants){
		float total = 0;
		for(Participant p : participants){
			total += p.payed;
		}
		List<SettlementPayment> payments = new ArrayList<SettlementPayment>();
		this.media = total / participants.size();
		while (true) {
			if (participants.size() == 0)
				break;
			this.min = Collections.min(participants, (p1, p2) -> Float.compare(p1.payed, p2.payed));
			this.max = Collections.max(participants, (p1, p2) -> Float.compare(p1.payed, p2.payed));
			if (min.payed == max.payed)
				break;
			if (max_pay() > min_pay()) {
				max.payed -= min_pay();
				SettlementPayment payment = new SettlementPayment(min, max, min_pay());
				payments.add(payment);
				participants.remove(min);
			}
			else if (max_pay() < min_pay()) {
				min.payed += max_pay();
				SettlementPayment payment = new SettlementPayment(min, max, max_pay());
				payments.add(payment);
				participants.remove(max);
			}
			else {
				participants.remove(min);
				participants.remove(max);
				SettlementPayment payment = new SettlementPayment(min, max, max_pay());
				payments.add(payment);
			}
		}
		return payments;
	}

	private float max_pay() {
		return this.max.payed - this.media;
	}

	private float min_pay() {
		return this.media - this.min.payed;
	}
}


public class ExpensesCalculator{
	float media = 0;
	Participant max = null;
	Participant min = null;

	public ExpensesCalculator(){}

	public calculateExpenses(List<Participant> participants){
		float total = 0;
		for(participant p : participants){
			total += p.payed();
		}
		List<SettlementPayment> payments = new ArrayList<SettlementPayment>();
		self.media = total / participants.size();
		while (True) {
			if (participants.size() == 0)
				break;
			self.min = Collections.min(participants, Comparator.comparing(c -> c.payed));
			self.max = Collections.max(participants, Comparator.comparing(c -> c.payed))
			if (min.payed() == max.payed())
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

	private max_pay() {
		self.max.payed - self.media;
	}

	private min_pay() {
		self.media - self.min.payed;
	}
}


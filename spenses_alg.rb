class Participante
  attr_accessor :payed, :name
  
  def initialize(amount, name)
    @payed = amount
    @name = name
  end
end

class PaymentsCalculator

  attr_accessor :max, :min, :media

  def calcular_gastos(participantes)
    total = participantes.inject(0){|a, e| a + e.payed }
    pagos = []
    @media = total / participantes.count
    while true
      break if participantes.empty?
      @min, @max = participantes.minmax_by(&:payed)
      break if min.payed == max.payed
      puts "#{min.name}, #{min.payed}", "#{max.name}, #{max.payed}"
      puts "#{min_pay}, #{max_pay}"
      if max_pay > min_pay
        max.payed -= min_pay
        pagos << [min, max, min_pay]
        participantes.delete(min)
      elsif max_pay < min_pay
        min.payed += max_pay
        pagos << [min, max, max_pay]
        participantes.delete(max)
      else
        participantes.delete(max)
        participantes.delete(min)
        pagos << [min, max, max_pay]
      end
    end
    pagos
  end

  def min_pay
    media - min.payed
  end

  def max_pay
    max.payed - media
  end
end

p = Participante.new(10, "p1")
p2 = Participante.new(10, "p2")
p3 = Participante.new(10, "p3")
p4 = Participante.new(10, "p4")

r = PaymentsCalculator.new.calcular_gastos([p, p2, p3, p4])
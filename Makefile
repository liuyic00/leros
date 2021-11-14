
APP=base
TESTS="base lhi lhi2 lognosign reg imm mem"
TESTPATH=asm

prop:
	make hw
	cd generated ;\
	$$FIRRTL -i Leros.hi.fir -E low -fil Leros -o Leros.il.lo.fir ;\
	$$FIRRTL -i Leros.hi.fir -E low ;\
	$$FIRRTL -i Leros.hi.fir -E verilog ;\
	$$FIRRTL -i Leros.hi.fir -E sverilog

prop-alu:
	sbt "runMain leros.formal.alu.AluAccuProp" ;\
	cd generated ;\
	$$FIRRTL -i AluAccuPropAll.hi.fir -E low ;\
	$$FIRRTL -i AluAccuPropAll.hi.fir -E low -fil AluAccuPropAll -o AluAccuPropAll.il.opt.lo.fir ;\
	$$FIRRTL -i AluAccuPropAll.il.opt.lo.fir -E smt2

hwsim:
	sbt -Dprogram=$(APP) "testOnly leros.LerosTest"


swsim:
	sbt -Dprogram=$(APP) "testOnly leros.sim.LerosSimSpec"

hw:
	sbt "runMain leros.Leros asm/$(APP).s"

test-alu:
	sbt "test:runMain leros.AluAccuTester"

all: all-hwsim all-swsim

all-hwsim:
	sbt -Dtestpath=$(TESTPATH) -Dtests=$(TESTS) "testOnly leros.LerosTest"

all-swsim:
	sbt -Dtestpath=$(TESTPATH) -Dtests=$(TESTS) "testOnly leros.sim.LerosSimSpec"

# clean everything (including IntelliJ project settings)
clean:
	git clean -fd

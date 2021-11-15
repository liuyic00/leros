
APP=base
TESTS="base lhi lhi2 lognosign reg imm mem"
TESTPATH=asm

prop-hw:
	make hw
	cd generated ;\
	$$FIRRTL -i Leros.hi.fir -E low -fil Leros -o Leros.il.lo.fir ;\
	$$FIRRTL -i Leros.hi.fir -E low ;\
	$$FIRRTL -i Leros.hi.fir -E verilog ;\
	$$FIRRTL -i Leros.hi.fir -E sverilog

prop-leros:
	sbt "runMain leros.formal.leros.LerosProp"
	make get-smt FILENAME=LerosPropGeneral

prop-alu:
	sbt "runMain leros.formal.alu.AluAccuProp"
	make get-smt FILENAME=AluAccuPropAll

get-smt:
	cd generated ;\
	$$FIRRTL -i $(FILENAME).hi.fir -E low ;\
	$$FIRRTL -i $(FILENAME).hi.fir -E low -fil $(FILENAME) -o $(FILENAME).il.lo.fir ;\
	$$FIRRTL -i $(FILENAME).il.lo.fir -E low-opt -o $(FILENAME).il.opt.lo.fir ;\
	$$FIRRTL -i $(FILENAME).il.opt.lo.fir -E smt2


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

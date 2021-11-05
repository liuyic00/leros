
APP=base
TESTS="base lhi lhi2 lognosign reg imm mem"
TESTPATH=asm

formal:
	make hw
	cd generated ;\
	$$FIRRTL -i Leros.hi.fir -E low -fil Leros -o Leros.il.lo.fir ;\
	$$FIRRTL -i Leros.hi.fir -E low ;\
	$$FIRRTL -i Leros.hi.fir -E verilog ;\
	$$FIRRTL -i Leros.hi.fir -E sverilog

hwsim:
	sbt -Dprogram=$(APP) "testOnly leros.LerosTest"


swsim:
	sbt -Dprogram=$(APP) "testOnly leros.sim.LerosSimSpec"

hw:
	sbt "runMain leros.Leros asm/$(APP).s"

test-alu:
	sbt "test:runMain leros.AluTester"

all: all-hwsim all-swsim

all-hwsim:
	sbt -Dtestpath=$(TESTPATH) -Dtests=$(TESTS) "testOnly leros.LerosTest"

all-swsim:
	sbt -Dtestpath=$(TESTPATH) -Dtests=$(TESTS) "testOnly leros.sim.LerosSimSpec"

# clean everything (including IntelliJ project settings)
clean:
	git clean -fd

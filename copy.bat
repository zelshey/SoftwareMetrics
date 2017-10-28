SET arg1=Test.class 
SET arg2=Test.class.bak
java -cp ".;bin;lib/*" Copy bin/%arg1% bin/%arg2%

pause
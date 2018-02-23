while :

do

	java -jar Exemple1.jar &
	java -jar Exemple2.jar &
	java -jar Exemple3.jar &
	sleep 1800

	kill `ps ax | grep 'java -jar Exemple2.jar' | awk '{print $1}'`
	kill `ps ax | grep 'java -jar Exemple1.jar' | awk '{print $1}'`
        kill `ps ax | grep 'java -jar Exemple3.jar' | awk '{print $1}'`


done

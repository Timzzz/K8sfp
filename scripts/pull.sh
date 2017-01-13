curl -G --proxy timzwietasch:"n2(rSR6oi@192".168.209.235:8888 \
	"http://10.0.6.56:31843/query?pretty=true" \
	--data-urlencode "db=k8s" \
	--data-urlencode "q=SELECT value, container_name  FROM \"cpu/usage_rate\" WHERE namespace_name =~ /.*default/ AND container_name =~ /.+/ LIMIT 100" \
	> pull.json

curl http://localhost:10000/api/v1/transform -v \
  -H "Content-Type: application/json" \
  -H "Origin: https://Bizone-ai.github.io" \
  -d "{ \"input\": \"Hello World\", \"definition\": \"\$\$substring(0,5):$\"}"

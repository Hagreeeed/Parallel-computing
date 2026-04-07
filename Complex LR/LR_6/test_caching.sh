#!/bin/bash

BASE_URL="http://localhost:8082" # Catalog service port
PRODUCT_ID=1 # Change to a valid product ID if needed

echo "=========================================================="
echo "    🧪 Testing Catalog Service Caching with Redis        "
echo "=========================================================="

echo -e "\n1️⃣ First request (Should hit Database, Slower):"
time curl -s "$BASE_URL/api/v1/products/$PRODUCT_ID" > /dev/null
echo ""

echo -e "\n2️⃣ Second request (Should hit Redis Cache, Faster):"
time curl -s "$BASE_URL/api/v1/products/$PRODUCT_ID" > /dev/null
echo ""

echo -e "\n3️⃣ Third request (Should hit Redis Cache, Faster):"
time curl -s "$BASE_URL/api/v1/products/$PRODUCT_ID" > /dev/null
echo ""

echo "=========================================================="
echo "    ✅ Test completed. Compare the times above.           "
echo "=========================================================="

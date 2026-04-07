#!/bin/bash
# Демонстраційний сценарій до ЛР №6

echo "1. Перевірка доступності Pod'ів"
kubectl get pods

echo "-----------------------------------"
echo "2. Перевірка доступності Service'ів"
kubectl get svc

echo "-----------------------------------"
echo "3. Масштабування сервісу catalog-service до 4 реплік"
kubectl scale deployment catalog-service --replicas=4
sleep 5
kubectl get pods -l app=catalog-service

echo "-----------------------------------"
echo "4. Тестування обриву зв'язку (Self-healing). Видаляємо один Pod."
POD_TO_DELETE=$(kubectl get pods -l app=order-billing-service -o jsonpath='{.items[0].metadata.name}')
kubectl delete pod $POD_TO_DELETE
echo "Pod видалено. Перевіряємо статус (K8s повинен підняти новий):"
kubectl get pods -l app=order-billing-service

echo "-----------------------------------"
echo "5. Rolling Update (Оновлення версії)"
# Симуляція оновлення версії образу для catalog
# (Оскільки образ :v2 не зібраний, K8s видасть ErrImagePull, 
# але це чудово демонструє принцип збереження доступності)
kubectl set image deployment/catalog-service catalog-service=catalog-service:v2
echo "Переглядаємо статус оновлення (Deployment створює нові Pod'и перед видаленням старих):"
kubectl get pods -l app=catalog-service
sleep 5
# Відкат релізу до попередньої версії
kubectl rollout undo deployment/catalog-service
echo "Відкат успішний."

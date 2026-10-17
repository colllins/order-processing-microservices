#!/bin/sh

if [ -n "$ECS_CONTAINER_METADATA_URI_V4" ]; then
  TASK_IP=$(wget -qO- "$ECS_CONTAINER_METADATA_URI_V4/task" \
    | sed -n 's/.*"IPv4Addresses":\["\([^"]*\)"\].*/\1/p')

  export EUREKA_INSTANCE_IP_ADDRESS="$TASK_IP"
fi

echo "ECS metadata URI: $ECS_CONTAINER_METADATA_URI_V4"
echo "Detected task IP: $TASK_IP"
echo "Eureka IP: $EUREKA_INSTANCE_IP_ADDRESS"
exec java -jar app.jar \
  --eureka.instance.ip-address="$TASK_IP" \
  --eureka.instance.prefer-ip-address=true
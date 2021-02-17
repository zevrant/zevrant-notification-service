FROM zevrant/zevrant-ubuntu-base:latest

EXPOSE 9008

RUN apt-get update && apt-get install -y curl python3 python3-pip && pip3 install awscli --user

RUN mkdir -p /usr/local/microservices/zevrant-home-services/zevrant-notification-service/

RUN mkdir -p /var/log/zevrant-home-services/zevrant-notification-service\
  && mkdir -p /storage/keys

RUN useradd -m -d /usr/local/microservices/zevrant-home-services/zevrant-notification-service/ -G developers  zevrant-notification-service

RUN chown -R zevrant-notification-service:developers /var/log/zevrant-home-services/zevrant-notification-service /usr/local/microservices/zevrant-home-services/zevrant-notification-service /storage/keys

USER zevrant-notification-service

COPY build/libs/zevrant-notification-service-*.jar /usr/local/microservices/zevrant-home-services/zevrant-notification-service/zevrant-notification-service.jar

RUN mkdir ~/.aws; echo "[default]" > ~/.aws/config; echo "region = us-east-1" >> ~/.aws/config; echo "output = json" >> ~/.aws/config

CMD export ROLE_ARN="arn:aws:iam::725235728275:role/NotificationServiceRole" \
 && export http_proxy=$PROXY_CREDENTIALS@3.210.165.61:3128 \
 && aws s3 cp s3://zevrant-resources/$ENVIRONMENT/ssl/zevrant-notification-service.p12 /usr/local/microservices/zevrant-home-services/zevrant-notification-service/zevrant-services.p12 \
 && java -jar -Dspring.profiles.active=$ENVIRONMENT -Dpassword=$password  /usr/local/microservices/zevrant-home-services/zevrant-notification-service/zevrant-notification-service.jar


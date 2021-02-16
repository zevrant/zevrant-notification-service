FROM zevrant/zevrant-ubuntu-base:latest

EXPOSE 9008

RUN mkdir -p /usr/local/microservices/zevrant-home-services/zevrant-notification-service/

RUN mkdir -p /var/log/zevrant-home-services/zevrant-notification-service\
  && mkdir -p /storage/keys

RUN useradd -m -d /usr/local/microservices/zevrant-home-services/zevrant-notification-service/ -G developers  zevrant-notification-service

RUN chown -R zevrant-notification-service:developers /var/log/zevrant-home-services/zevrant-notification-service /usr/local/microservices/zevrant-home-services/zevrant-notification-service /storage/keys

USER zevrant-notification-service

COPY build/libs/zevrant-notification-service-*.jar /usr/local/microservices/zevrant-home-services/zevrant-notification-service/zevrant-notification-service.jar

RUN mkdir ~/.aws; echo "[default]" > ~/.aws/config; echo "region = us-east-1" >> ~/.aws/config; echo "output = json" >> ~/.aws/config

CMD export ROLE_ARN="arn:aws:iam::725235728275:role/notificationServiceRole" \
 && openssl req -newkey rsa:4096 -nodes -keyout ~/private.pem -x509 -days 365 -out ~/public.crt -subj "/C=US/ST=New York/L=Brooklyn/O=Example Brooklyn Company/CN=examplebrooklyn.com"\
 && password=`date +%s | sha256sum | base64 | head -c 32`\
 && openssl pkcs12 -export -inkey ~/private.pem -in ~/public.crt -passout "pass:$password" -out /usr/local/microservices/zevrant-home-services/zevrant-notification-service/zevrant-services.p12\
 && java -jar -Dspring.profiles.active=$ENVIRONMENT -Dpassword=$password  /usr/local/microservices/zevrant-home-services/zevrant-notification-service/zevrant-notification-service.jar


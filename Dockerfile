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

RUN curl https://raw.githubusercontent.com/zevrant/zevrant-services-pipeline/master/bash/zevrant-services-start.sh > /usr/local/microservices/zevrant-home-services/zevrant-notification-service/startup.sh \
  && curl https://raw.githubusercontent.com/zevrant/zevrant-services-pipeline/master/bash/openssl.conf > ~/openssl.conf \
  && echo "" \
  && cat ~/startup.sh \
  && echo "" \
  && cat ~/openssl.conf

CMD export ROLE_ARN="arn:aws:iam::725235728275:role/NotificationServiceRole" \
 && password=`date +%s | sha256sum | base64 | head -c 32` \
 && bash /usr/local/microservices/zevrant-home-services/zevrant-notification-service/startup.sh $password \
 && cat /usr/local/microservices/zevrant-home-services/zevrant-notification-service/openssl.conf \
 && java -jar -Dspring.profiles.active=$ENVIRONMENT -Dpassword=$password /usr/local/microservices/zevrant-home-services/zevrant-notification-service/zevrant-notification-service.jar


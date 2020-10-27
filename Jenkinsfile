pipeline {
    agent none
    environment {
        VARS_FILE='./vars'
        SECRETS_KEYCLOAK='Keycloak-Server/Keys'
        CF_TEMPLATE_PATH='cloudformation/Keycloak-Server.yaml'
        HOME = "${WORKSPACE}"
        NPM_CONFIG_CACHE = "${WORKSPACE}/.npm"
    }
    stages {
        stage('Init') {
            steps {
                echo 'Init'
            }
        }
        stage('Build') {
           agent {
                dockerfile{
                    filename 'Dockerfile'
                    label 'ucms-docker-agent'
                }   
            }
            steps {
                sh '''#!/bin/bash
                    echo "Building Keycloak"
                    mvn -Pdistribution -pl distribution/server-dist -am -Dmaven.test.skip clean install
                '''
            }
        }
        stage('Testing') {
            steps {
                echo "Tests removed until we have a simplified test suite"
            }
        }
        stage('Deploy') {
            agent {
                label 'aws-serverless'
            }
            steps {
                script {
                    echo "Deploying Keycloak"
                    // withAWS(credentials:'AJPlus Systems Access') {
                    //     sh '''#!/bin/bash
                    //         echo "#!/bin/bash" > $VARS_FILE
                    //         #secrets are only pulled from one region of an account so using --region arg in below
                    //         aws --region eu-west-1 --output json secretsmanager get-secret-value --secret-id $SECRETS_KEYCLOAK | jq -r '.SecretString' | jq -r 'to_entries|map(.key+"="+.value|tostring)|.[]' >> $VARS_FILE
                    //         chmod +x $VARS_FILE
                    //         . $VARS_FILE
                    //         CF_TEMPLATE_PATH=`echo "${CF_TEMPLATE_PATH}" | sed -e 's/^[ \t]*//'`
                    //         stack_name=`basename ${CF_TEMPLATE_PATH} | cut -d'.' -f1`
                    //         aws cloudformation deploy \
                    //             --template-file ${CF_TEMPLATE_PATH} \
                    //             --stack-name ${stack_name} --region eu-west-1 \
                    //             --tags  Name="${stack_name} CF Stack" \
                    //                     "Business / Service Owner"=Digital-DevOps@aljazeera.net \
                    //                     Purpose=Network \
                    //                     ProductID=${stack_name} \
                    //                     Environment=Shared \
                    //                     CreatedBy=rosab@aljazeera.net \
                    //             --parameter-overrides PagerDutyKey=${PagerDutyKey} \
                    //                     DbUsername=${DbUsername} \
                    //                     DbInstance=${DbInstance} \
                    //                     DbPassword=${DbPassword} \
                    //                     AdminUsername=${AdminUsername} \
                    //                     AdminPassword=${AdminPassword} \
                    //                     KeyName=${KeyName} \
                    //                     EnvironmentName=Shared \
                    //                     ProductName=Keycloak \
                    //                     InstanceType=t2.small \
                    //                     CreatorEmail=rosab@aljazeera.net \
                    //                     TeamEmail=Digital-Devops@aljazeera.net \
                    //                     NewRelicKey=${NewRelicKey} \
                    //                     InstanceSecurityGroup=${InstanceSecurityGroup} \
                    //     '''
                    // }
                    sshagent (credentials:["6ee01661-f84d-41fe-880b-05d047312c3c"]) {
                        sh '''#!/bin/bash
                            echo "hello world" >> jenkinslog.txt
                            
                            // curl -s https://download.newrelic.com/infrastructure_agent/gpg/newrelic-infra.gpg | sudo apt-key add - && \
                            // echo "license_key: ${NewRelicKey}" | sudo tee -a /etc/newrelic-infra.yml && \
                            // printf "deb [arch=amd64] https://download.newrelic.com/infrastructure_agent/linux/apt bionic main" | sudo tee -a /etc/apt/sources.list.d/newrelic-infra.list && \
                            // sudo apt-get update && \
                            // sudo apt-get install newrelic-infra -y

                            // sudo echo "POSTGRE_USER=${DbUsername}" >> /etc/environment
                            // sudo echo "POSTGRE_PASS=${DbPassword}" >> /etc/environment
                            // sudo echo "POSTGRE_ADDRESS=${DbInstance}" >> /etc/environment
                            // sudo echo "pagerduty_token=${PagerDutyKey}" >> /etc/environment 
                            // sudo apt-get update
                            
                            // git clone https://github.com/ALJAZEERAPLUS/keycloak.git
                            // cd keycloak
                            // wget --no-verbose -O /tmp/apache-maven-3.6.3.tar.gz http://archive.apache.org/dist/maven/maven-3/3.6.3/binaries/apache-maven-3.6.3-bin.tar.gz

                            // tar xzf /tmp/apache-maven-3.6.3.tar.gz -C /opt/
                            // ln -s /opt/apache-maven-3.6.3 /opt/maven
                            // ln -s /opt/maven/bin/mvn /usr/local/bin
                            // rm -f /tmp/apache-maven-3.6.3.tar.gz
                            // sudo echo "MAVEN_HOME=/opt/maven" >> /etc/environment

                            // sudo apt-get clean
                            // sudo apt-get update
                            // sudo apt-get install -y openjdk-8-jdk
                            // sudo update-alternatives --config java
                            // sudo update-alternatives --config javac
                            // sudo mvn -Pdistribution -pl distribution/server-dist -am -Dmaven.test.skip clean install
                            // sudo tar xfz distribution/server-dist/target/keycloak-12.0.0-SNAPSHOT.tar.gz
                            // sudo mv ./modules/postgresql keycloak-12.0.0-SNAPSHOT/modules/system/layers/keycloak/org/
                            // sudo rm keycloak-12.0.0-SNAPSHOT/standalone/configuration/standalone.xml
                            // sudo mv ./modules/standalone.xml keycloak-12.0.0-SNAPSHOT/standalone/configuration/
                            // sudo ./keycloak-12.0.0-SNAPSHOT/bin/add-user-keycloak.sh -r master -u ${AdminUsername} -p ${AdminPassword}
                            // nohup sudo ./keycloak-12.0.0-SNAPSHOT/bin/standalone.sh -b `ip -4 -o addr show dev eth0 |grep -Pom1 '(?<= inet )[0-9.]*'` &
                        '''
                    }
                }
            }
        }
    }
}
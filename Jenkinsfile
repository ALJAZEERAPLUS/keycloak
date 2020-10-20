pipeline {
    agent {
        docker {
            image 'maven:3-alpine'
            args '-v /root/.m2:/root/.m2'
        }
    }
    environment {
        VARS_FILE='./vars'
        SECRETS_='Keycloak-Server/Keys'
        CF_TEMPLATE_PATH='cloudformation/Keycloak-Server.yaml'
    }
    stages {
        stage('Init') {
            steps {
                sh '''#!/bin/bash
                    echo 'Init' 
                    sudo apt-get update
                    sudo apt-get install nodejs -y
                    sudo apt-get install npm -y
                    sudo dpkg --configure -a
                    sudo apt-get -f install
                    sudo apt-get full-upgrade
                    sudo apt-get autoremove
                    sudo apt-get clean
                    ls
                '''
            }
        }
        stage('Build') {
            steps {
                sh '''#!/bin/bash
                    echo "Building Keycloak"
                    mvn -Pdistribution -pl distribution/server-dist -am -Dmaven.test.skip clean install
                '''
            }
        }
        stage('Testing') {
            steps {
                sh '''#!/bin/bash
                    echo "Testing Keycloak"
                    mvn -f testsuite/utils/pom.xml exec:java -Pkeycloak-server
                '''
            }
        }
        stage('Deploy') {
            agent {
                label 'aws-serverless'
            }
            steps {
                script {
                    sh '''#!/bin/bash
                    echo "Deploying Keycloak"
                    '''
                    // withAWS(credentials:'AJPlus Systems Access') {
                    //     sh '''#!/bin/bash
                    //         echo "#!/bin/bash" > $VARS_FILE
                    //         #secrets are only pulled from one region of an account so using --region arg in below
                    //         aws --region eu-west-1 --output json secretsmanager get-secret-value --secret-id $SECRETS_WAZUH | jq -r '.SecretString' | jq -r 'to_entries|map(.key+"="+.value|tostring)|.[]' >> $VARS_FILE
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
                    //                     InstanceType=t2.micro \
                    //                     CreatorEmail=rosab@aljazeera.net \
                    //                     TeamEmail=Digital-Devops@aljazeera.net \
                    //                     KeyName=${KeyName} \
                    //     '''
                    // }
                }
            }
        }
    }
}
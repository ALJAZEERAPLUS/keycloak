pipeline {
    agent {
        dockerfile true
        //label 'ucms-docker-agent'
    }
    environment {
        VARS_FILE='./vars'
        SECRETS_='Keycloak-Server/Keys'
        CF_TEMPLATE_PATH='cloudformation/Keycloak-Server.yaml'
        // Override HOME to WORKSPACE
        HOME = "${WORKSPACE}"
        // or override default cache directory (~/.npm)
        NPM_CONFIG_CACHE = "${WORKSPACE}/.npm"
    }
    stages {
        stage('Init') {
            steps {
                sh '''#!/bin/bash
                    echo 'Init'
                    npm -v
                    node -v
                    mvn --version
                    ls
                '''
            }
        }
        stage('Build') {
            steps {
                sh '''#!/bin/bash
                    echo "Building Keycloak"
                    mvn clean install -B -DskipTests -Pdistribution
                '''
            }
        }
        stage('Testing') {
            steps {
                sh '''#!/bin/bash
                    echo "Testing Keycloak"
                    mvn clean install -B -Pauth-server-wildfly -DskipTests -f testsuite/pom.xml
                    mvn clean install -B -f testsuite/integration-arquillian/tests/base/pom.xml -Dkeycloak.client.provider=map -Dkeycloak.group.provider=map | misc/log/trimmer.sh;
                '''
            }
        }
        stage('Deploy') {
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
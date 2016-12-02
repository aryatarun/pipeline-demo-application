def blueGreenDeploy(appname, version, apppath, mainroute) {
    withCred  entials([[$class: 'UsernamePasswordMultiBinding', credentialsId: '3cd9dd1f-8015-4bc1-9e2b-329c6fa267de', passwordVariable: 'CF_PASSWORD', usernameVariable: 'CF_USERNAME']]) {
        withEnv(["APPNAME=${appname}-${version}", "APPPATH=${apppath}", "MAINROUTE=${mainroute}"]) {
            sh '''#!/bin/bash -ex
                cf login -a https://api.aws.ie.a9s.eu -o thomas_rauner_andrena_de -s production -u $CF_USERNAME -p $CF_PASSWORD
                set +e
                cf create-service a9s-postgresql postgresql-single-small mysql
                set -e

                route=\$(cf curl /v2/routes?q=host:$MAINROUTE | jq -r ".resources[].metadata.url")
                if [ -z "$route" ]; then
                  bound_apps=
                else
                  bound_apps=\$(cf curl \$route/apps | jq -r ".resources[].entity.name")
                fi
                for bound_app in $bound_apps; do
                  echo "Bound App: $bound_app"
                done

                approute=${APPNAME//\\./_}
                domain=aws.ie.a9sapp.eu
                cf push $APPNAME -n $approute -p \"$APPPATH\"
                set +e
                curl -c 4 ${approute}.${domain}
                success=$?
                set -e
                if [ "$success" -eq "0" ]; then
                    echo "Removing other apps"
                    cf map-route $APPNAME $domain -n $MAINROUTE
                    for boundapp in $bound_apps; do
                      cf unmap-route $boundapp $domain -n $MAINROUTE
                      cf scale -i 0 $boundapp
                      cf stop $boundapp
                      cf delete $boundapp
                    done
                else
                    echo "Reverting"
                    cf stop $APPNAME
                    cf delete $APPNAME
                    exit 1
                fi
              '''
        }


    }
}

return this;
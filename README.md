# pod-tp1

## Compilación

Se provee un script para compilar todos los proyectos y extraerlos. 
```bash
cd tpe1
chmod u+x extract-binaries.sh
./extract-binaries
```

*Nota*: Es necesario contar con Java 8 y Maven instalados y con sus respectivas variables de entorno configuradas.

## Ejecución

### RMI Registry
Directorio: `tpe1`
```bash
./run-registry.sh
```

### Servidor
Directorio: `tpe1`.
*Nota*: por defecto escucha en `localhost:1099`.

```bash
./run-server.sh
```

### Simulación de flujo básico
*Nota*: esta simulación funcionará si se iniciaron el RMI Registry y el servidor previamente y tal como está por defecto en los scripts provistos.
```bash
./simulate-vote.sh
```

### Distintos clientes
*Nota*: por defecto, todos los clientes intentan conectarse a `localhost:1099`.

Este script permite modificar los scripts que se encuentren en el estado inmediatamente posterior a la compilación del proyecto.

Previamente a correr cualquier cliente se recomienda correr este script (personalizando la dirección y el puerto). En requisito encontrarse en el directorio `tpe1/client/target/tpe1-client-1.0-SNAPSHOT`. Los comandos para modificar los valores solo funcionan si éstos se encuentran en su estado original.

Se incluyen algunos archivos csv de ejemplo, y éstos se encuentran en el mismo directorio que los scripts de los clientes.
```bash
# Luego de compilar
cd ./client/target/tpe1-client-1.0-SNAPSHOT
mkdir backup
cp *.sh backup/
address="localhost:1099" # la nueva dirección
```

*Cliente de fiscalización*
```bash
# Cambia partido
fiscal_party=lynx
sed -i "s/lynx/${fiscal_party}/g" run-monitor-client.sh

# Cambia mesa
fiscal_table=1000
sed -i "s/1000/${fiscal_table}/g" run-monitor-client.sh

# Cambia IP y puerto
sed -i "s/localhost:1099/${address}/g" run-monitor-client.sh

./run-monitor-client.sh # Por defecto fiscaliza la mesa 1000 y el partido LYNX
```

*Cliente de administración*
```bash
# Cambia la acción a ejecutar (si antes era open)
action=state
sed -i "s/open/${action}/g" run-admin-client.sh

sed -i "s/localhost:1099/${address}/g" run-admin-client.sh

./run-admin-client # Por defecto abre la elección. Opciones: OPEN, CLOSE, STATE
```

*Cliente de votación*
```bash
# Cambia archivo de origen de votos (por defecto utiliza el archivo 'votes.csv')
votes=votes.csv
sed -i "s=./votes.csv=${votes}=g" run-voting-client.sh

sed -i "s/localhost:1099/${address}/g" run-voting-client.sh

./run-voting-client # 
```

*Clientes de consulta*
(por defecto dejan sus resultados en `./result.csv`)
```bash
outpath=result.csv

# Cliente de consulta de mesa
number=1000
sed -i "s/1000/${number}/g" run-table-query-client.sh
sed -i "s=./result.csv=${outpath}=g" run-table-query-client.sh
sed -i "s/localhost:1099/${address}/g" run-table-query-client.sh

./run-table-query-client.sh

# Cliente de consulta de estado
state=jungle
sed -i "s/jungle/${number}/g" run-state-query-client.sh
sed -i "s=./result.csv=${outpath}=g" run-state-query-client.sh
sed -i "s/localhost:1099/${address}/g" run-state-query-client.sh

./run-state-query-client.sh

# Cliente de consulta nacional
sed -i "s=./result.csv=${outpath}=g" run-national-query-client.sh
sed -i "s/localhost:1099/${address}/g" run-national-query-client.sh

./run-national-query-client.sh
```

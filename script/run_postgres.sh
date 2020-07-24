docker run \
    --rm \
    -d \
    -p 15432:5432 \
    -e POSTGRES_PASSWORD=hello \
    postgres
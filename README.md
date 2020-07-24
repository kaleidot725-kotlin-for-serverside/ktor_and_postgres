# Ktor と Postgre Database を組み合わせて使ってみる



## Docker で Postgres の環境構築する

今回は Postgres を環境構築するのに Docker を利用する。以下に Docker を利用して Postgres の環境を構築する方法をまとめる。

###  Postgres を起動する

```
docker run \
    --rm \
    -d \
    -p 15432:5432 \
    -e POSTGRES_PASSWORD=hello \
    postgres
```

| オプションとコマンド       | 意味                                                         |
| -------------------------- | ------------------------------------------------------------ |
| docker run postgres        | postgres イメージを起動する                                  |
| -rm                        | すでに存在する場合は自動的に削除する                         |
| -d                         | バックグラウンドで動作させたコンテナのIDを表示する           |
| -p                         | ホスト側とコンテナ側でポートをどのように紐付けるか（ホスト側：コンテナ側） |
| -e POSTGRES_PASSWORD=hello | 環境変数 POSTGRES_PASSWORD を追加してパスワードを設定する    |

### Postgres を終了する

```
docker ps
CONTAINER ID        IMAGE               COMMAND                  CREATED             STATUS              PORTS                     NAMES
0e6c9569d374        postgres            "docker-entrypoint.s…"   11 minutes ago      Up 11 minutes       0.0.0.0:15432->5432/tcp   brave_greider
docker kill 0e6c9569d374
```


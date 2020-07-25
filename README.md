# Ktor と Postgres を組み合わせて使ってみる

# はじめに

Ktor で Postgres に接続してデータベース操作を行うにはどうすればよいのか調べたことをまとめます。

# Docker で Postgres の環境構築する

今回は Postgres を環境構築するのに Docker を利用する。以下に Docker を利用して Postgres の環境を構築する方法をまとめる。

##  Postgres を起動する

```shell
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

## Postgres にアクセスする

```shell
psql -h localhost -p 15432 -U postgres
```

| オプションとコマンド | 意味                                                       |
| -------------------- | ---------------------------------------------------------- |
| psql                 | PostgresSQL でデータベースやテーブルを作成するためのツール |
| -h                   | データベースサーバーのホスト名称                           |
| -p 15432             | データベースサーバーのポスト番号                           |
| -U                   | データベースのユーザーネーム                               |

## Postgres を終了する

```shell
docker ps
CONTAINER ID        IMAGE               COMMAND                  CREATED             STATUS              PORTS                     NAMES
0e6c9569d374        postgres            "docker-entrypoint.s…"   11 minutes ago      Up 11 minutes       0.0.0.0:15432->5432/tcp   brave_greider
docker kill 0e6c9569d374
```

| オプションとコマンド            | 意味                                          |
| ------------------------------- | --------------------------------------------- |
| docker ps                       | Docker で起動しているコンテナの一覧を表示する |
| docker kill &lt;ContainerId&gt; | Docker で起動しているコンテナを終了させる     |

# Ktor から Postgres にアクセスする

## Exposed をセットアップする

Ktor で Postgres にアクセスしたい場合は Exposed という ORM ライブラリを利用するのが良いらしい。なので Exposed を依存関係に追加しておく、後 Exposed で Postgres に接続するには Postgres のライブラリも導入する必要があるのでそちらもインストールしておく。

```groovy
dependencies {
	def exposed_version = "0.24.1"
	implementation "org.jetbrains.exposed:exposed-core:$exposed_version"
	implementation "org.jetbrains.exposed:exposed-dao:$exposed_version"
	implementation "org.jetbrains.exposed:exposed-jdbc:$exposed_version"

	def postgresql_version = "42.2.2"
	implementation "org.postgresql:postgresql:${postgresql_version}"
}
```

## Exposed で Postgres に接続する

Database.connect に以下の引数を指定して実行すれば Postgres に接続できる。

```kotlin
val db = Database.connect(
    url = "jdbc:postgresql://localhost:15432/postgres",
    driver = "org.postgresql.Driver",
    user = "postgres",
    password = "hello"
)
```

| 引数名   | 説明                                                         |
| -------- | ------------------------------------------------------------ |
| url      | `jdbc:postgressql://<ホスト名称>:<ポート番号>/<データベース名称>` という形式で指定する。 |
| driver   | `org.postgresql.Driver`を指定指定する。                      |
| user     | データベースに接続するユーザー名を指定する。                 |
| password | データベースに接続するパスワードを指定する。                 |

## Exposed の DSL でテーブルを定義する。

Exposed でのテーブルを定義するには DSL を使った方法と DAO を使った方法があります。今回は DSL を使ってテーブルを定義したいと思います。次のように Table に継承した object を宣言して、そこに Row を定義していく形になります。あとは Primary Key を指定してあげらば Table ができます。

```kotlin
object Cities: Table() {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 50)

    override val primaryKey = PrimaryKey(id)
}
```

DLSの詳細についてはこちらに記載があるので確認する。（https://github.com/JetBrains/Exposed/wiki/DSL ）

## Exposed の DSL のテーブルを作成して、挿入して、取得する

Exposed の DSL でテーブル操作を transaction の Block 内で必ず行う必要があるので、transaction でテーブル操作を実行するようにします。今回はテーブルを作成して、データを作成して挿入して、それらのデータを取得してみたいと思います。

```kotlin
transaction(db) {
    // Create cities table
    SchemaUtils.create(Cities)

    // Create new city item
    Cities.insert { it[name] = "St. Petersburg" }
	
    // Get all city item
    Cities.selectAll().forEach { println("${it[Cities.name]}")}
}	
```

これを実行すると Cities というテーブルが作成、そして Cities  テーブルに Item が追加されます。次は実行時のログになりますが　Ctities テーブルから全件取得した結果が想定どおりになっていることがわかります。

```
2020-07-25 14:27:42.405 [main] DEBUG Exposed - INSERT INTO cities ("name") VALUES ('St. Petersburg')
2020-07-25 14:27:42.408 [main] DEBUG Exposed - SELECT cities.id, cities."name" FROM cities
St. Petersburg
```

# おわりに

- Ktor でデータベースに接続するためには Exposed を使うのが良さそう
- Exposed で Postgres に接続するには Postgres のライブラリが必要になる
- Exposed ではデータベースを操作する方法として DSL と DAO が用意されている。




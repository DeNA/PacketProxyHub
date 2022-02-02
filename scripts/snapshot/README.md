## Snapshot

Google 公式 AndroidEmulator の指定スナップショットを抽出して、PacketProxyHubにアップロードできる PPHS ファイルフォーマット形式のファイルに変換したり、逆にPPHSファイルを自分の環境に展開できます。

## インストール方法

```sh
$ rake install
```

## 使い方　

### PPHSファイルの作成方法
```sh
$ snapshot create
```

### PPHSファイルからの展開方法
```sh
$ snapshot extract XXXXXX.snapshot
```

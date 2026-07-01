# Tetris (MVC Edition)

Java (Swing) を用いて、MVC (Model-View-Controller) アーキテクチャに基づき実装したテトリスです。

## 特徴

- **MVCアーキテクチャ**: Model / View / Controller を明確に分離し、責務ごとにパッケージを分割
- **7-bagアルゴリズム**: 7種類のテトリミノを1セットとしてシャッフルし、偏りなく出現
- **簡易Wall Kick**: 壁際・床際で回転がそのまま入らない場合に、いくつかのオフセットを試行して回転を成立させる
- **DAS (Delayed Auto Shift)**: 左右キーの押しっぱなしで、一定時間後から高速リピート移動
- **ソフトドロップ / ハードドロップ**: どちらも実装済み、移動距離に応じてスコア加算
- **レベルシステム**: ライン消去数に応じてレベルが上昇し、落下速度も上昇
- **一時停止機能**: Pキーでいつでも一時停止・再開が可能

## 動作環境

- Java 21 (JDK) 以降推奨
- OS: Windows / macOS / Linux (GUI表示可能な環境)

## すぐに遊びたい場合

コマンドを一切使わずに遊びたい場合は、プロジェクト直下の **`Tetris.jar`** をダブルクリックしてください。
実行するPCにJavaがインストールされていれば、そのまま起動します（Javaが入っていない場合は事前にインストールが必要です）。

> ソースコードを変更した場合、`Tetris.jar` は自動更新されません。以下のコマンドで作り直してください。
>
> ```powershell
> Get-ChildItem -Path src -Recurse -Filter *.java | ForEach-Object { $_.FullName } | Out-File -Encoding ascii sources.txt
> javac -encoding UTF-8 -d out_classes "@sources.txt"
> jar --create --file Tetris.jar --main-class com.tetris.Main -C out_classes .
> Remove-Item sources.txt, out_classes -Recurse
> ```

## プロジェクト構成

```
tetris-mvc-project/
└── src/
    └── main/
        └── java/
            └── com/
                └── tetris/
                    ├── Main.java                 # エントリーポイント
                    │
                    ├── constants/
                    │   └── Constants.java         # 全定数（ボードサイズ、スコア、速度等）
                    │
                    ├── model/                     # Model層
                    │   ├── Board.java             # ボード状態・衝突判定・行削除
                    │   ├── Tetromino.java         # テトリミノの種別・回転・位置（不変）
                    │   ├── TetrominoType.java     # 7種のミノの形状・色データ
                    │   ├── TetrominoFactory.java  # 7-bagアルゴリズムによる生成
                    │   ├── WallKickData.java      # 簡易Wall Kickオフセット
                    │   ├── GameState.java         # スコア・レベル・ゲームオーバー状態
                    │   └── Point.java             # 座標クラス
                    │
                    ├── view/                      # View層
                    │   ├── GameFrame.java          # メインウィンドウ（MVC結線）
                    │   ├── BoardPanel.java         # ボード描画
                    │   ├── SidePanel.java          # スコア・NEXT表示
                    │   └── ColorTheme.java         # UI配色・フォント定義
                    │
                    ├── controller/                # Controller層
                    │   ├── GameController.java     # ゲームループ・操作ロジック
                    │   ├── KeyInputHandler.java    # キー入力受付（DAS対応）
                    │   └── ViewRefreshListener.java # Controller→View通知用インターフェース
                    │
                    └── event/                     # Model→外部への通知
                        ├── GameEvent.java          # イベント種別
                        └── GameEventListener.java  # リスナーインターフェース
```

## ビルド & 実行方法

### Windows (PowerShell)

```powershell
# コンパイル（文字コードにUTF-8を明示）
javac -encoding UTF-8 -d out (Get-ChildItem -Path src -Recurse -Filter *.java | ForEach-Object { $_.FullName })

# 実行
java -cp out com.tetris.Main
```

> ファイル数が多くコマンドが失敗する場合は、以下のレスポンスファイル方式をお試しください。
> （`sources.txt` は必ず `-Encoding ascii` で書き出してください。UTF-8のBOM付きなどで保存すると、
> `javac` がレスポンスファイル自体を読み込めずエラーになります。ファイルパスは通常ASCII文字のみなので問題ありません。）
>
> ```powershell
> Get-ChildItem -Path src -Recurse -Filter *.java | ForEach-Object { $_.FullName } | Out-File -Encoding ascii sources.txt
> javac -encoding UTF-8 -d out "@sources.txt"
> java -cp out com.tetris.Main
> ```

### macOS / Linux

```bash
javac -encoding UTF-8 -d out $(find src -name "*.java")
java -cp out com.tetris.Main
```

## 操作方法

| キー | 動作 |
|---|---|
| `←` / `A` | 左移動（押しっぱなしで加速：DAS） |
| `→` / `D` | 右移動（押しっぱなしで加速：DAS） |
| `↓` / `S` | ソフトドロップ（押している間だけ高速落下） |
| `↑` / `W` | 時計回り回転 |
| `Z` | 反時計回り回転 |
| `Space` | ハードドロップ（即着地） |
| `P` | 一時停止 / 再開 |
| `C` / `Shift` | ホールド（現在のミノを保留・入れ替え） |

## スコアルール

| アクション | 加算スコア |
|---|---|
| 1ライン消去 | 100 × レベル |
| 2ライン同時消去 | 300 × レベル |
| 3ライン同時消去 | 500 × レベル |
| 4ライン同時消去（テトリス） | 800 × レベル |
| ソフトドロップ | 1マスにつき1点 |
| ハードドロップ | 1マスにつき2点 |

累計10ライン消去ごとにレベルが1上昇し、落下速度も上昇します（下限あり）。

## 設計上の主なポイント

- **`Tetromino` は不変(immutable)クラス**として設計。移動・回転メソッドは新しいインスタンスを返すのみで、実際に位置を確定させるかどうかは `Board` の衝突判定結果を見て `GameController` が判断します。
- **`Board` が唯一の真実の状態(Single Source of Truth)** を持ち、衝突判定・行削除・ゲームオーバー判定を集約しています。
- **Observerパターン**により、`Board`（Model）と `GameController`（Controller）はそれぞれ `GameEventListener` / `ViewRefreshListener` を通じて外部に通知するのみで、View（Swingコンポーネント）の実装を直接知りません。

## 既知の制限事項

- ゴーストピース（着地予測位置の表示）は実装していません。
- Wall Kickは正式なSRS(Super Rotation System)ではなく簡易実装です。特定の回転パターンでは、正式なSRSと挙動が異なる場合があります。
- ホールドは一般的なテトリス実装と同様、1つのテトリミノが固定される（着地して盤面に置かれる）までは連続使用できません。

## ライセンス

学習・検証目的のサンプル実装です。自由にご利用ください。

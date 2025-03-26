package mkyz08.sample;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.kafka.KafkaComponent;
import org.apache.camel.component.kafka.KafkaConfiguration;
import org.apache.camel.impl.DefaultCamelContext;

public class Application {

    public static void main(String[] args) {
        try {
            CamelContext context = new DefaultCamelContext();
            context.addComponent("kafka", createKafkaComponent());
            context.addRoutes(createProducerRouteBuilder());
            //context.addRoutes(createConsumerRouteBuilder());

            context.start(); // Camelコンテキストを開始
            //Thread.sleep(10000); // 10秒間スリープ
            //context.stop(); // Camelコンテキストを停止
        } catch (Exception e) {
            e.printStackTrace(); // エラーが発生した際にスタックトレースを出力
        }
    }

    // Kafkaコンポーネントを作成するメソッド
    static KafkaComponent createKafkaComponent() {
        KafkaConfiguration kafkaConfig = new KafkaConfiguration();
        
        // OpenShiftルートを使用して外部Kafkaクラスターへの接続を設定
        kafkaConfig.setBrokers("my-cluster-kafka-bootstrap.camel-kafka.svc.cluster.local:9092");
        
        // SSL接続のための設定
        //kafkaConfig.setSecurityProtocol("SSL");
        //kafkaConfig.setSslTruststoreLocation("/home/lab-user/workspace/camel-springboot-test2/src/main/java/mkyz08/sample/client.truststore.jks"); // ローカルのトラストストアへのパスを設定（適宜変更）
        //kafkaConfig.setSslTruststorePassword("password"); // トラストストアのパスワードを設定

        // その他のKafka設定
        //kafkaConfig.setGroupId("group1"); // グループIDを設定
        //kafkaConfig.setAutoCommitEnable(true); // 自動コミットを有効化
        //kafkaConfig.setAutoCommitIntervalMs(5000); // 自動コミットの間隔を設定
        //kafkaConfig.setAutoOffsetReset("earliest"); // オフセットがない場合の処理
        //kafkaConfig.setRequestRequiredAcks("all"); // メッセージ成功の条件
        //kafkaConfig.setConsumersCount(1); // コンシューマの数

        KafkaComponent kafka = new KafkaComponent();
        kafka.setConfiguration(kafkaConfig);

        return kafka; // Kafkaコンポーネントを返す
    }

    // プロデューサルートを設定するメソッド
    static RouteBuilder createProducerRouteBuilder() {
        return new RouteBuilder() {
            public void configure() throws Exception {
                from("timer:trigger?period=1000") // 1000ミリ秒ごとにトリガー
                    .routeId("kafka_producer_route")
                    .setBody(constant("Hello World")) // メッセージのBODYにHello Worldを設定
                    .to("kafka:test01"); // トピックtest01にメッセージを送信
            }
        };
    }

    // コンシューマルートを設定するメソッド
    //static RouteBuilder createConsumerRouteBuilder() {
    //    return new RouteBuilder() {
    //        public void configure() throws Exception {
    //            from("kafka:test01") // トピックtest01からメッセージを受信
    //                .routeId("kafka_consumer_route")
    //                .log("body = ${body}"); // 受信したメッセージの内容をログに記録
    //        }
    //    };
    //}
}

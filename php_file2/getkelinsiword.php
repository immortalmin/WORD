<?php
header('content-type:application/json;charset=utf8');
include './include/kelinsi_conn.php'; //数据库链接
$json_string = file_get_contents('php://input');
$body = json_decode($json_string,true);
$wid = $body['wid'];	
$result =mysqli_query($kelinsi_conn,'select * from word where wid="'.$wid.'"');
$word = mysqli_fetch_array($result,MYSQLI_ASSOC);
if($word){
	$result =mysqli_query($kelinsi_conn,'select iid,number,label,word_ch,explanation,gram from items where wid="'.$word["wid"].'"');
	$items = [];
	while ($item = mysqli_fetch_array($result,MYSQLI_ASSOC)){ //返回查询结果到数组
		$sentences_query = mysqli_query($kelinsi_conn,'select sid,sentence_ch,sentence_en from sentences where iid="'.$item["iid"].'"');
		//array_splice($sentences, 0, count($sentences));
		$sentences = [];
		while($sentence=mysqli_fetch_array($sentences_query,MYSQLI_ASSOC)){
			$sentences[]=$sentence;
		}
		$item["sentences"]=$sentences;
		$en_tip_query = mysqli_query($kelinsi_conn,'select eid,tip from en_tip where iid='.$item["iid"]);
		$en_tips = [];
		while($en_tip = mysqli_fetch_array($en_tip_query,MYSQLI_ASSOC)){
			$en_tips[] = $en_tip;
		}
		$item["en_tip"]=$en_tips;
		$items[]=$item;
	}
	$word["items"]=$items;
	echo json_encode($word, JSON_UNESCAPED_UNICODE);
}
//4.释放内存中的查询结果
mysqli_free_result($result);
//5.关闭连接
mysqli_close($kelinsi_conn);
?>


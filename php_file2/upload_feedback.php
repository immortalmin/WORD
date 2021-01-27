<?php
header('content-type:application/json;charset=utf8');
include './include/conn.php';
$what = $_POST['what'];
$uid = $_POST['uid'];
$description = $_POST['description'];
$contact = $_POST['contact'];
$photo = "";
$add_time = date('Y-m-d H:i:s');
if($what==0){//功能建议	
	mysqli_query($conn,'INSERT INTO feature_suggestions(uid,description,contact,add_time) VALUES('.$uid.',"'.$description.'","'.$contact.'","'.$add_time.'");');
	$fid = mysqli_insert_id($conn);
	if(!empty($_FILES)){//判断是否有文件上传
		$dirPath = '../img/feedback/';//图片保存路径
		//如果不存在这个路径，就创建该路径
		if(!is_dir($dirPath)){
			@mkdir($dirPath);
		}
		$count=0;
		$img_path="";
		while(true){
			$image_name = 'image'.$count;
			if(!isset($_FILES[$image_name])){
				break;
			}
			$file = $_FILES[$image_name];
			//判断是否是合法文件
			if(is_uploaded_file($file['tmp_name'])){
				$info = pathinfo($_FILES[$image_name]['name']);
				$ext = $info['extension'];
				if(move_uploaded_file($_FILES[$image_name]['tmp_name'],$dirPath."f_".$fid.'_'.$count.'.'.$ext)){
					if($img_path!="") $img_path=$img_path."#";
					$img_path = $img_path."f_".$fid.'_'.$count.'.'.$ext;
       		                 	//mysqli_query($conn,'UPDATE feature_suggestions SET photo="'."f_".$fid.'_'.$count.'.'.$ext.'" WHERE fid='.$fid);
					echo "move successful\n";
                		}else{
                        		echo "move failure\n";
                		}
			}else{
				echo "文件不合法\n";
				break;
			}
			$count++;
		}
		mysqli_query($conn,'UPDATE feature_suggestions SET img_path="'.$img_path.'" WHERE fid='.$fid);
	}else{
		echo "没有文件上传\n";
	}
}else{//错误反馈
	$phone_model = $_POST['phone_model'];
	mysqli_query($conn,'INSERT INTO error_feedback(uid,phone_model,description,contact,add_time) VALUES('.$uid.',"'.$phone_model.'","'.$description.'","'.$contact.'","'.$add_time.'");');
	$eid = mysqli_insert_id($conn);
	if(!empty($_FILES)){//判断是否有文件上传
		$dirPath = '../img/feedback/';//图片保存路径
		//如果不存在这个路径，就创建该路径
		if(!is_dir($dirPath)){
			@mkdir($dirPath);
		}
		$count=0;
		$img_path="";
		while(true){
			$image_name = 'image'.$count;
			if(!isset($_FILES[$image_name])){
				break;
			}
			$file = $_FILES[$image_name];
			//判断是否是合法文件
			if(is_uploaded_file($file['tmp_name'])){
				$info = pathinfo($_FILES[$image_name]['name']);
				$ext = $info['extension'];
				if(move_uploaded_file($_FILES[$image_name]['tmp_name'],$dirPath.'e_'.$eid.'_'.$count.'.'.$ext)){
                	       	 	//mysqli_query($conn,'UPDATE error_feedback SET photo="'."e_".$eid.'_'.$count.'.'.$ext.'" WHERE eid='.$eid);
					if($img_path!="") $img_path=$img_path."#";
                                        $img_path = $img_path."e_".$eid.'_'.$count.'.'.$ext;
					echo "move successful\n";
                		}else{
                       		 	echo "move failure\n";
                		}
			}else{
				echo "文件不合法\n";
				break;
			}
			$count++;
		}
		mysqli_query($conn,'UPDATE error_feedback SET img_path="'.$img_path.'" WHERE eid='.$eid);
	}else{
		echo "没有文件上传\n";
	}
}
mysqli_close($conn);
?>


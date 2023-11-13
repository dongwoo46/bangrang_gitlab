import React, { useState, useEffect } from 'react';
import styles from './eventdataplus.module.css'
import axiosInstance from '../axiosinstance';
import PostCode from './postcode';
import { DateTimePicker, LocalizationProvider } from '@mui/x-date-pickers';
import { DemoContainer } from '@mui/x-date-pickers/internals/demo';
import { AdapterDayjs } from '@mui/x-date-pickers/AdapterDayjs';
import dayjs, { Dayjs } from 'dayjs';

interface Props {
    closeModal : () => void;
    isEventIdx : number|null;
    modaltype:boolean; // true 면 이벤트 수정, false면 신규 등록
    reload : () => void;
}

interface EventData {
    title:string;
    subTitle: string ;
    content: string;
    startDate: string;
    endDate: string;
    address: string;
    eventUrl: string;
    image: string;
    subImage: string;
}

const EventDataPlus: React.FC<Props> = ({closeModal, isEventIdx, modaltype, reload}) => {
    const [isEvent, setEvent] = useState<EventData>(
        {title:"",subTitle: "" ,content: "",startDate: "",endDate: "",
            address: "",eventUrl: "",image: "",subImage: ""})
  
    const [image, setImage] = useState<FileList | null>(null);
    const [subImage, setSubImage] = useState<FileList | null>(null);

    const [ImagePreview, setImagePreview] = useState<string|null>(null);
    const [SubImagePreview, setSubImagePreview] = useState<string|null>(null);
    
    const [startDate , setStartDate] = useState<Dayjs|null>(null)
    const [endDate , setEndDate] = useState<Dayjs|null>(null)

    useEffect(()=>{
        if (modaltype && isEventIdx) {
            axiosInstance({
                method: "get",
                url: `${process.env.REACT_APP_API}/web/event/${isEventIdx}`,
            }).then((res) => {
                const eventData = res.data
                setEvent({
                    title: eventData.title,
                    subTitle: eventData.subtitle,
                    content: eventData.content,
                    startDate: eventData.startDate,
                    endDate: eventData.endDate,
                    address: eventData.address,
                    eventUrl: eventData.eventUrl,
                    image: eventData.image,
                    subImage : eventData.subImage
                  });
                  setStartDate(dayjs(eventData.startDate))
                  setEndDate(dayjs(eventData.endDate))
            }).catch((err) => {
                console.log(err);
            });
        }
    },[])


    const registEvent = () => {
        const formData = new FormData();
        if (image != null) {
          formData.append("image", image[0]);
        }
        if (subImage != null) {
          formData.append("subImage", subImage[0]);
        }
        
        const event = {
            title:isEvent.title,
            subTitle:isEvent.subTitle,
            content:isEvent.content,
            startDate:isEvent.startDate,
            endDate:isEvent.endDate,
            address:isEvent.address,
            eventUrl:isEvent.eventUrl,
        }
        formData.append("data", JSON.stringify(event));
    
        axiosInstance({
          method: "post",
          url: `${process.env.REACT_APP_API}/web/event`,
          data: formData,
          headers: {
            "Content-Type": "multipart/form-data",
          },
        })
          .then((res) => {
            alert("이벤트 등록 완료");
            reload();
            closeModal();
          })
          .catch((err) => {
            console.log(err)
            alert("이벤트 등록 실패 ㅠㅠ\n잠시 후, 다시 시도해주세요.")
        });
    };

    const modifyEvent = () => {
        const formData = new FormData();
        if (image != null) {
          formData.append("image", image[0]);
        }
        if (subImage != null) {
          formData.append("subImage", subImage[0]);
        }
        const event = {
            title:isEvent.title,
            subTitle:isEvent.subTitle,
            content:isEvent.content,
            startDate:isEvent.startDate,
            endDate:isEvent.endDate,
            address:isEvent.address,
            eventUrl:isEvent.eventUrl,
        }
        formData.append("data", JSON.stringify(event));
    
        axiosInstance({
          method: "put",
          url: `${process.env.REACT_APP_API}/web/event/${isEventIdx}`,
          data: formData,
          headers: {
            "Content-Type": "multipart/form-data",
          },
        })
          .then((res) => {
            console.log(res);
            alert('이벤트 수정 완료')
            reload();
            closeModal();
          })
          .catch((err) => console.log(err));
    };

    return (
        <div className={styles.modal_overlay} onClick={closeModal}>
            <div className={styles.modal} onClick={(e) => e.stopPropagation()}>
                <span onClick={closeModal}>&times;</span>
                {/* 모달 내용 */}
                <h1 style={{ textAlign: "center" }}>이벤트 {modaltype ? '수정':'등록'}하기</h1>
                <div className={styles.databox}>
                    <label className={styles.data_type}>제목 :</label>
                    <input className={styles.data_input}
                        type="text" value={isEvent.title}
                        onChange={(e)=>{    
                            setEvent((prev) => ({
                            ...prev,title: e.target.value,}));}} />
                </div>

                <div className={styles.databox}>
                    <label className={styles.data_type}>부제목 :</label>
                    <input className={styles.data_input}
                        type="text" value={isEvent.subTitle!}
                        onChange={(e)=>{    
                            setEvent((prev) => ({
                            ...prev, subTitle: e.target.value}));}} />
                </div>

                <div className={styles.databox}>
                    <label className={styles.data_type}>내용 작성 :</label>
                    <textarea
                        className={styles.data_input}
                        style={{ resize:'none' }}
                        rows={5}
                        value={isEvent.content}
                        onChange={(e)=>{    
                            setEvent((prev) => ({
                            ...prev, content: e.target.value}));}} />
                </div>
                <div className={styles.databox}>
                    <label className={styles.data_type}>주소 : </label>
                    <PostCode
                        address={isEvent.address}
                        setAddress={(value: string) => {
                            setEvent((prev) => ({
                            ...prev, address: value}));}} />
                </div>
                <div className={styles.databox}>
                    <label className={styles.data_type}>일정 : </label>
                    <LocalizationProvider dateAdapter={AdapterDayjs}>
                        <DemoContainer components={["DateTimePicker", "DateTimePicker"]}>    
                            <DateTimePicker
                            label="시작 날짜"
                            sx={{ width: "65%" }}
                            value={startDate}
                            onChange={(date)=>{          
                                setEvent((prev) => ({
                                ...prev,
                                startDate: dayjs(date).format("YYYY-MM-DDTHH:mm:ss")}));}}
                            format="YYYY년 MM월 DD일 hh:mm A"/>
        
                            <DateTimePicker
                            sx={{ width: "65%" }}
                            label="종료 날짜"
                            value={endDate}
                            onChange={(date)=>{          
                                setEvent((prev) => ({
                                ...prev,
                                endDate: dayjs(date).format("YYYY-MM-DDTHH:mm:ss")}));}}
                            format="YYYY년 MM월 DD일 hh:mm A"
                            />
                        </DemoContainer>
                    </LocalizationProvider>
                </div>
                <div className={styles.databox}>
                    <label className={styles.data_type}>이벤트 URL : </label>
                    <input
                        className={styles.data_input}
                        type="text"
                        value={isEvent.eventUrl}
                        onChange={(e)=>{    
                            setEvent((prev) => ({
                            ...prev,eventUrl: e.target.value}));}}/>
                </div>

                <div className={styles.databox}>
                    <label className={styles.data_type}><br />메인 이미지 : <br />
                    { isEvent.image || ImagePreview ? <label htmlFor="imageInput">수정하기</label>:null}
                    <br />
                    { isEvent.image || ImagePreview ? <button onClick={()=>{setImage(null);setImagePreview(null);}}>삭제하기</button> :null}
                    </label>
                    {ImagePreview ? 
                     <img className={styles.data_input} src={ImagePreview} alt="미리보기"/>:
                    isEvent.image ? 
                    <img className={styles.data_input}
                        src={isEvent.image}
                        alt="미리보기"
                    /> :
                    <div className={styles.data_input} style={{alignItems:'center',display:'flex',justifyContent:'center'}}>
                        <label htmlFor="imageInput">이미지 등록하기</label>
                    </div>}
                    <input
                        type="file"
                        id="imageInput"
                        style={{ display: "none" }}
                        onChange={({ target: { files } }) => {
                            if (files && files[0]) {
                            setImage(files);
                            const reader = new FileReader();
                            
                            reader.onloadend = () => {
                                setImagePreview(reader.result as string);
                            };

                            reader.readAsDataURL(files[0]);

                            } else {
                            setImage(null);
                            setImagePreview(null);
                            }
                        }}/>
                </div>

                <div className={styles.databox}>
                    <label className={styles.data_type}><br />서브 이미지 : <br />
                        {isEvent.subImage || SubImagePreview && <label htmlFor="subimageInput">수정하기</label>}
                        <br />
                        {isEvent.subImage || SubImagePreview && <button onClick={()=>{setSubImage(null);setSubImagePreview(null);}}>삭제하기</button>}
                    </label>
                    {SubImagePreview ? 
                     <img className={styles.data_input} src={SubImagePreview} alt="미리보기"/>:
                    isEvent.subImage ? 
                    <img className={styles.data_input}
                        src={isEvent.subImage}
                        alt="미리보기"
                    /> :
                    <div className={styles.data_input} style={{alignItems:'center',display:'flex',justifyContent:'center'}}>
                        <label htmlFor="subimageInput">서브 이미지 등록하기</label>
                    </div>}
                    <input
                        type="file"
                        id="subimageInput"
                        style={{ display: "none" }}
                        onChange={({ target: { files } }) => {
                            if (files && files[0]) {
                                setSubImage(files);
                          
                                const reader = new FileReader();
                            
                                reader.onloadend = () => {
                                    setSubImagePreview(reader.result as string);
                                };
                                reader.readAsDataURL(files[0]);
                            } else {
                                setSubImage(null);
                                setSubImagePreview(null);
                            }
                    }}/>
                </div>

            {modaltype ? 
            <div style={{ marginTop: "10px" }}>
                {isEvent.title &&
                isEvent.subTitle &&
                isEvent.startDate &&
                isEvent.endDate &&
                isEvent.content &&
                isEvent.address ? <button onClick={()=>modifyEvent()}>이벤트 수정하기</button>
                : <button disabled>이벤트 수정하기</button>}
            </div>
            : 
            <div style={{ marginTop: "10px" }}>
                {isEvent.title &&
                isEvent.subTitle &&
                isEvent.startDate &&
                isEvent.endDate &&
                isEvent.content &&
                isEvent.address &&
                image ? <button onClick={()=>registEvent()}>이벤트 등록하기</button>
                : <button disabled>이벤트 등록하기</button>}
            </div>}
        </div>
    </div>
  );
};

export default EventDataPlus;

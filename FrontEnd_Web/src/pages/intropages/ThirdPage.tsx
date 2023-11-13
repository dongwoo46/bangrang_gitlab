import React, { useState, useEffect } from "react";
import { useTransition, animated } from "@react-spring/web";
import shuffle from "lodash.shuffle";
import data from "./DataList";
import styles from "./ThirdPage.module.css";
import Marquee from "react-fast-marquee";
import { useSpring } from "react-spring";

const imagePaths = [
  "assets/MarqueeImg/서울.svg",
  "assets/MarqueeImg/부산.svg",
  "assets/MarqueeImg/대전.svg",
  "assets/MarqueeImg/광주.svg",
  "assets/MarqueeImg/제주.svg",
  "assets/MarqueeImg/경주.svg",
  "assets/MarqueeImg/인천.svg",
  "assets/MarqueeImg/대구.svg",
  "assets/MarqueeImg/강릉.svg",
];

interface ThirdPageProps {
  visible: boolean;
}

const ThirdPage: React.FC<ThirdPageProps> = ({ visible }) => {
  const [rows, set] = useState(data);
  const [character, setCharacter] = useState("");

  useEffect(() => {
    const t = setInterval(() => set(shuffle), 3000);
    return () => clearInterval(t);
  }, []);

  let height = 0;
  const transitions = useTransition(
    rows.map((data) => ({
      ...data,
      y: (height += data.height) - data.height,
    })),
    {
      key: (item: any) => item.name,
      from: { height: 0, opacity: 0 },
      leave: { height: 0, opacity: 0 },
      enter: ({ y, height }) => ({ y, height, opacity: 1 }),
      update: ({ y, height }) => ({ y, height, opacity: 1 }),
    }
  );

  const marqueeTransitions = useTransition(imagePaths, {});

  const springConfigs = {
    from: { transform: "scale(1)" },
    to: { transform: "scale(0.85)" },
    config: { duration: 1000, tension: 100, friction: 10 },
    reset: true,
    reverse: true,
  };

  const springStyles = imagePaths.map((_, index) =>
    useSpring({ ...springConfigs, delay: index * 345 })
  );

  useEffect(() => {
    const t = setInterval(() => set(shuffle), 3000);
    return () => clearInterval(t);
  }, []);

  return (
    <div>
      <div className={styles.Page}>
        <p
          className={styles.Percent}
          style={{
            position: "absolute",
            transition: "0.7s",
            opacity: visible ? "1" : "0",
            fontSize: "30px",
            color: "#1daeff",
            marginTop: "3%",
          }}
        >
          방랑 명예의 전당
        </p>
        <div
          className={styles.detailContainer}
          style={{
            opacity: visible ? "1" : "0",
            transition: "1s",
          }}
        >
          <div className={styles.frontpage1}></div>
          <div className={styles.messageStyle}>
            <p>매일 업데이트 되는</p>
            <p>방랑 랭킹을 확인하며</p>
            <p>최고 방랑자를 노리세요 !</p>
          </div>
          <div className={styles.backpage1}></div>
        </div>
        <div
          className={styles.listContainer}
          style={{
            opacity: visible ? "1" : "0",
            transition: "1s",
          }}
        >
          <div className={styles.list} style={{ top: "5%" }}>
            {transitions((style, item, t, index) => (
              <animated.div
                className={styles.card}
                style={{ zIndex: data.length - index, ...style }}
              >
                <div className={styles.cell}>
                  <div
                    className={styles.details}
                    style={{
                      backgroundImage: item.css,
                      color: "white",
                      fontSize: "20px",
                      fontWeight: "bold",
                      // alignItems: "center",
                      justifyContent: "center",
                      display: "flex",
                      flexDirection: "column",
                      // marginTop: "30px",
                    }}
                  >
                    <p
                      style={{
                        textAlign: "start",
                        padding: "20px",
                        fontFamily: "bold",
                      }}
                    >
                      {index + 1}등 {item.character}
                    </p>
                  </div>
                </div>
              </animated.div>
            ))}
          </div>
        </div>
        <div
          className={styles.messageContainer}
          style={{
            opacity: visible ? "1" : "0",
            transition: "1s",
          }}
        >
          <div className={styles.frontpage2}></div>
          <div className={styles.messageStyle}>
            <p>여러분의 친구들이</p>
            <p>당신을 제쳤군요 !</p>
            <p>두고만 볼건가요 ?</p>
          </div>
          <div className={styles.backpage2}></div>
        </div>
      </div>
      <p
        style={{
          fontSize: "25px",
          color: "skyblue",
          fontWeight: "bold",
          textShadow: "#00d0ff71 1px 0 10px",
        }}
      >
        각 지역의 다양한 도장도 모아보세요 !!
      </p>
      <Marquee
        className="marquee"
        play={true}
        loop={300000}
        style={{
          height: "auto",
          paddingBottom: "10px",
          overflow: "hidden",
          justifyContent: "space-between",
        }}
        direction="left"
        speed={300}
      >
        {marqueeTransitions((_, item, t, index) => (
          <animated.img
            key={index}
            className={styles.image}
            src={item}
            alt={`Marquee Image ${index}`}
            style={{
              ...springStyles[index],
            }}
          />
        ))}
      </Marquee>
    </div>
  );
};

export default ThirdPage;

import styles from "./button.module.scss";

export const Button = ({ onClick, title }) => {
  return (
    <button className={styles.btn_spotify} onClick={onClick}>
      {title}
    </button>
  );
};

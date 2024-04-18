import { btn_spotify } from "./button.module.scss";

export const Button = ({ title, onClick }) => {
  return (
    <button className={btn_spotify} onClick={onClick}>
      {title}
    </button>
  );
};

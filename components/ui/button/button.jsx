import { btn_spotify } from "./button.module.scss";

export const Button = ({ onClick, title }) => {
  return (
    <button className={btn_spotify} onClick={onClick}>
      {title}
    </button>
  );
};

import { InputHTMLAttributes } from "react";
import classes from "./Input.module.scss";

type inputProps = InputHTMLAttributes<HTMLInputElement> & {
  label: string;
};

export default function Input({ label, ...props }: inputProps) {
  return (
    <div className={classes.root}>
      <label htmlFor={props.id}>{label}</label>
      <input {...props} />
    </div>
  );
}

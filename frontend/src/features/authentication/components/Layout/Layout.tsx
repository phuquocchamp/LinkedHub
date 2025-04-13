import { ReactNode } from "react";
import classes from "./Layout.module.scss";

export default function Layout({
  children,
  className,
}: {
  children: ReactNode;
  className?: string;
}) {
  return (
    <div className={`${classes.root} ${className || ""}`}>
      <header className={classes.container}>
        <a href="/"></a>
        <img src="./logo.svg" alt="" className={classes.logo} />
      </header>
      <main className={classes.container}>{children}</main>
      <footer>
        <ul className={classes.container}>
          <li>
            <img src="./logo-dark.svg" alt="" />
            <span>2025</span>
          </li>
          <li>
            <a href="">Accessibility</a>
          </li>
          <li>
            <a href="">User Agreement</a>
          </li>
          <li>
            <a href="">Privacy Policy</a>
          </li>
          <li>
            <a href="">Cookie Policy`</a>
          </li>
          <li>
            <a href="">Copyright Policy</a>
          </li>
          <li>
            <a href="">Brand Policy</a>
          </li>
          <li>
            <a href="">Guest Controls</a>
          </li>
          <li>
            <a href="">Community Guidelines</a>
          </li>
          <li>
            <a href="">Language</a>
          </li>
        </ul>
      </footer>
    </div>
  );
}

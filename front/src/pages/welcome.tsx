import { InputBar } from "../components";

export function Welcome() {
  return <InputBar addHandler={() => alert("Hello world !")}></InputBar>;
}

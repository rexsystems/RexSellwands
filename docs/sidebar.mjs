const root = "rexsellwands";

export const rexsellwands = {
  label: "RexSellwands",
  items: [
    { label: "Overview", slug: root },
    {
      label: "Getting started",
      items: [
        { label: "Installation", slug: `${root}/getting-started/installation` },
        { label: "Requirements", slug: `${root}/getting-started/requirements` },
      ],
    },
    {
      label: "Configuration",
      items: [
        { label: "Settings", slug: `${root}/configuration/overview` },
        { label: "Messages", slug: `${root}/configuration/messages` },
        { label: "Prices", slug: `${root}/configuration/prices` },
      ],
    },
    {
      label: "Reference",
      items: [
        { label: "Commands", slug: `${root}/reference/commands` },
        { label: "Permissions", slug: `${root}/reference/permissions` },
      ],
    },
    { label: "Wands", slug: `${root}/guides/wands` },
    { label: "FAQ", slug: `${root}/faq` },
  ],
};
